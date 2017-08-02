package com.liang.translator.SPARQL2Gremlin;

import com.liang.translator.SPARQL2Gremlin.PatternEdge.PatternEdge;
import com.liang.translator.SPARQL2Gremlin.PatternEdge.PatternEdgeArrayList;
import com.liang.translator.SPARQL2Gremlin.PatternNode.PatternNode;
import com.liang.translator.SPARQL2Gremlin.PatternNode.PatternNodeArrayList;
import org.apache.jena.graph.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by cnic-liliang on 2017/7/24.
 */

public class Patterngraph {
    private static Logger logger = LoggerFactory.getLogger(Patterngraph.class);

    public PatternNodeArrayList nodes = new PatternNodeArrayList();
    public PatternEdgeArrayList edges = new PatternEdgeArrayList();
    public HashSet<String> patterns = new HashSet<String>();

    //patterns is used to record labels of edges
    public Patterngraph() {
        patterns.add("http://gcm.wdcm.org/ontology/gcmAnnotation/v1/ancestorTaxid");
        patterns.add("http://gcm.wdcm.org/ontology/gcmAnnotation/v1/x-taxon");
        patterns.add("http://gcm.wdcm.org/ontology/gcmAnnotation/v1/x-enzyme");
    }

    public Boolean IsPattern(String pattern) {
        return patterns.contains(pattern);
    }

    //
    PatternNode curNode = null;
    String traversal = "";
    public String GetTraversal() {
        return traversal;
    }

    public void GremlinTraversal() {
        ArrayList<PatternNode> nonVarList = nodes.AvailableNodeList();
        int len = nonVarList.size();
        if(curNode == null) {
            if(len < 1)
                return;
            else if(len == 1) {
                //it is the starting point, choose the first one
                curNode = nonVarList.get(0);
            }
            //traversal
            traversal += traversal + ".has('vertexID','" + curNode.Name() + "')";
        }

        if(BothTraversal() == false) {
            ArrayList<PatternEdge> availEdge = edges.AvailableEdges();
            for (PatternEdge e : availEdge) {
                if (e.OutNode().contains(curNode.Name()) && e.IsProperty() == false) {
                    traversal += ".out('" + e.Label() + "')";
                    edges.SetTraversed(e);
                    curNode = nodes.Get(e.InNode());
                    //has filter
                    ArrayList<PatternEdge> peArr = new ArrayList<PatternEdge>();
                    for(PatternEdge pe: curNode.Out()) {
                        if(pe.IsProperty()
                                && nodes.Get(pe.InNode()).Property()
                                && nodes.Get(pe.InNode()).IsVar()==false)
                            peArr.add(pe);
                    }
                    for(PatternEdge pe: peArr) {
                        traversal += ".has('" + pe.Label() +"', '" + pe.InNode() + "')";
                        edges.SetTraversed(pe);
                    }
                    traversal += ".as('" + curNode.Name() + "')";
                    //value collection
                    peArr = new ArrayList<PatternEdge>();
                    for(PatternEdge pe: curNode.Out()) {
                        //value collection mode
                        if(pe.IsProperty()
                                && nodes.Get(pe.InNode()).Property()
                                && nodes.Get(pe.InNode()).IsVar()==true)
                            peArr.add(pe);
                    }
                    for(PatternEdge pe: peArr) {
                        traversal += ".select('"+ curNode.Name() +"').values('" + pe.Label() +"').as('" + pe.InNode() + "')";
                        edges.SetTraversed(pe);
                    }
                }
            }
        }
    }

    Boolean BothTraversal() {
        // e1: A->B, e2: C->A, e3: C->B
        if(curNode.In().size() > 0) {
            ArrayList<ArrayList<PatternEdge>> results = new ArrayList<ArrayList<PatternEdge>>();
            //Here the edges should not be referenced here
            Object[] arr = curNode.In().toArray();
            for (Object elem : arr) {
                PatternEdge e = (PatternEdge) elem;
                if(e.HasTraversed()) continue;
                ArrayList<PatternEdge> eArr = new ArrayList<PatternEdge>();
                PatternNode node = nodes.nodes.get(e.OutNode());
                for(PatternEdge ein: node.In())
                    if(ein.IsProperty() == false) eArr.add(ein);
                for(PatternEdge eout: node.Out())
                    if(eout.IsProperty() == false) eArr.add(eout);
                results.add(eArr);
            }
            //return, as no edge satisfies this pattern
            if(results.isEmpty()) return false;

            ArrayList<PatternEdge> elist = results.get(0);
            for(int i = 1; i < results.size(); i++) {
                elist.retainAll(results.get(i));
            }

            if(elist.size() >= 1) {
                PatternEdge e2 = elist.get(0);
                for (Object obj : arr) {
                    PatternEdge e3 = (PatternEdge) obj;
                    if(e3.Label().contentEquals(e2.Label()) && e3.OutNode().contentEquals(e2.OutNode())) {
                        PatternEdge e1 = edges.GetEdge(e3.InNode(), e2.InNode());
                        if(e1 != null) logger.info("Optimizer with both() " + e1.Label());
                        else return false;

                        if(curNode.In().contains(e1)) {
                            logger.info("It is in edge " + e1.InNode());
                            traversal = traversal + ".in('" + e1.Label() + "').both().as('" + e1.OutNode() + "')" ;
                        }
                        else if(curNode.Out().contains(e1)) {
                            logger.info("It is out edge" + e1.OutNode());
                            traversal = traversal + ".out('" + e1.Label() + "').both().as('" + e1.InNode() +"')";
                        }
                        //Update e1, e2, e3
                        edges.SetTraversed(e1);
                        traversal = traversal + ".in('" + e3.Label() + "')";
                        edges.SetTraversed(e3);
                        edges.SetTraversed(e2);
                        curNode = nodes.nodes.get(e3.OutNode());
                        //has filter
                        ArrayList<PatternEdge> peArr = new ArrayList<PatternEdge>();
                        for(PatternEdge pe: curNode.Out()) {
                            if(pe.IsProperty()
                                    && nodes.Get(pe.InNode()).Property()
                                    && nodes.Get(pe.InNode()).IsVar()==false)
                                peArr.add(pe);
                        }
                        for(PatternEdge pe: peArr) {
                            traversal += ".has('" + pe.Label() +", '" + pe.InNode() + "')";
                            edges.SetTraversed(pe);
                        }
                        traversal += ".as('" + curNode.Name() + "')";
                        //value collection
                        peArr = new ArrayList<PatternEdge>();
                        for(PatternEdge pe: curNode.Out()) {
                            //value collection mode
                            if(pe.IsProperty()
                                    && nodes.Get(pe.InNode()).Property()
                                    && nodes.Get(pe.InNode()).IsVar()==true)
                                peArr.add(pe);
                        }
                        for(PatternEdge pe: peArr) {
                            traversal += ".select('"+ curNode.Name() +"').values('" + pe.Label() +"').as('" + pe.InNode() + "')";
                            edges.SetTraversed(pe);
                        }
                        return true;
                    }
                }
            }
        }
        return  false;
    }

    public void RDFNodePut(Node node) {
        if(node.isVariable()) {
            if(nodes.NodesContain(node.getName()) == false)
                nodes.NodePut(new PatternNode(true, node.getName()));
        }
        else{
            if(nodes.NodesContain(node.getURI()) == false)
                nodes.NodePut(new PatternNode(false, node.getURI()));
        }
    }

    public PatternEdgeArrayList edges(){
        return edges;
    }

    public Integer EdgeAdd(PatternEdge edge) {
        if(edges.Contain(edge) == false)
            return edges.Add(edge);
        return edges.Index(edge);
    }

    public void GraphUnion(Patterngraph graph) {
        nodes.Merge(graph.nodes);
        for(PatternEdge e: graph.edges.Edges()) {
            if(edges.Contain(e) == false) {
                Integer eid = edges.Add(e);
                Update(eid);
                logger.info("Graph Union " + eid);
            }
        }
        logger.info("Graph Union Done");
    }

    public void Update(int eid) {
        nodes.Update(edges.Edges().get(eid));
    }

    public Boolean TraversalComplete() {
        Boolean complete = true;
        for(PatternEdge e: edges.Edges()) {
            if(e.HasTraversed() == false) {
                complete = false;
                break;
            }
        }
        return complete;
    }

    public void print() {
        System.out.println("----PATTERNNODES----");
        for(PatternNode node: nodes.Nodes().values()) {
            System.out.println(node.IsVar() +", " + node.Property() + ", " + node.Name() + ", " + node.In().size() + ", " + node.Out().size());
        }
        System.out.println("----PATTERNEDGES----");
        for(PatternEdge edge: edges.Edges()) {
            System.out.println(edge.Label() +", " + edge.IsProperty() + ", " + edge.HasTraversed()  +"," + edge.InNode() + ", " + edge.OutNode());
        }
    }
}
