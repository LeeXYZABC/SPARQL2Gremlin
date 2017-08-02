package com.liang.translator.SPARQL2Gremlin;

import com.liang.translator.SPARQL2Gremlin.PatternEdge.PatternEdge;
import com.liang.translator.SPARQL2Gremlin.PatternNode.PatternNode;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.OpVisitorBase;
import org.apache.jena.sparql.algebra.OpWalker;
import org.apache.jena.sparql.algebra.op.*;
import org.apache.jena.sparql.expr.ExprAggregator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by cnic-liliang on 2017/6/14.
 *   https://github.com/dkuppitz/sparql-gremlin/tree/master/src/main/java/com/datastax/sparql/gremlin
 */

public class SPARQL2Gremlin extends OpVisitorBase {
    private static Logger logger = LoggerFactory.getLogger(SPARQL2Gremlin.class);
    //
    class VarResult{
        public String key;
        public String traversal;
        public VarResult(String cKey, String cTraversal) {
            key = cKey;
            traversal = cTraversal;
        }

        public String Key() {
            return key;
        }
        public String Traversal() {
            return traversal;
        }
    }
    //Pattern Graph
    public Patterngraph graph = new Patterngraph();
    //translate the sparql to gremlin
    public static String results = "g.V()";
    int varID = 0; // mark for update varResults in visit(OpExtend opExtend)
    public static ArrayList<VarResult> varResults = new ArrayList<VarResult>();
    static String orderByStr = "";
    static String rangeStr = "";

    String Transform(Query query) {
        final Op op = Algebra.compile(query);
        OpWalker.walk(op, this);
        //Tranversal the pattern graph
        while (graph.TraversalComplete() == false) {
            graph.GremlinTraversal();
        }
        //generate gremlin traversal
        results += graph.GetTraversal();
        for(VarResult var: varResults) {
            results += var.traversal;
        }
        results += orderByStr;
        //results collection
        if (!query.isQueryResultStar()) {
            final List<String> vars = query.getResultVars();
            switch (vars.size()) {
                case 0:
                    throw new IllegalStateException();
                case 1:
                    if (query.isDistinct()) {
                        results = results + ".dedup('" + vars.get(0) + "')";
                    }
                    results = results + ".select('" + vars.get(0) + "')";
                    break;
                case 2:
                    if (query.isDistinct()) {
                        results = results + ".dedup('" + vars.get(0) + "','" + vars.get(1) + "')";
                    }
                    results = results + ".select('" + vars.get(0)  + "','" + vars.get(1) + "')";
                    break;
                default:
                    final String[] all = new String[vars.size()];
                    vars.toArray(all);
                    if (query.isDistinct()) {
                        results = results + ".dedup(";
                        for(int i = 0; i < vars.size(); i ++) {
                            results += "'" + vars.get(i) + "'";
                            if(i < vars.size() - 1) results += ",";
                        }
                        results += ")";
                    }
                    final String[] others = Arrays.copyOfRange(all, 2, vars.size());
                    results = results + ".select(";
                    for(int i = 0; i < vars.size(); i ++) {
                        results += "'" + vars.get(i) + "'";
                        if(i < vars.size() - 1) results += ",";
                    }
                    results += ")";
                    break;
            }
        } else {
            if (query.isDistinct()) {
                results = results + ".dedup(" + ")";
            }
        }
        results += rangeStr;
        return results;
    }

    @Override
    public void visit(final OpUnion opUnion) {
        logger.info("OpUnion ...");
    }

    @Override
    public void visit(OpDistinct opDistinct) {
        logger.info("OpDistinct");
    }

    @Override
    public void visit(OpExt opExt)                    {
        logger.info("opExt");
    }

    @Override public void visit(OpBGP opBGP)                    {
        logger.info("OpBGP");
        final List<Triple> triples = opBGP.getPattern().getList();
        int i = 0;
        for (final Triple triple : triples) {
            final Node predicate = triple.getPredicate();
            final String uri = predicate.getURI();
            graph.RDFNodePut(triple.getSubject());
            Node object = triple.getObject();
            if(object.isVariable() || object.isURI()) {
                graph.RDFNodePut(triple.getObject());
                int eid = graph.EdgeAdd(new PatternEdge(triple.getPredicate().getURI(), !graph.IsPattern(uri),
                        triple.getSubject().isVariable()? triple.getSubject().getName():triple.getSubject().getURI(),
                        triple.getObject().isVariable()?triple.getObject().getName():triple.getObject().getURI()));
                graph.Update(eid);
            }
            else if(object.isLiteral()){
                //literal
            }
        }
    }

    @Override public void visit(OpQuadPattern quadPattern)      {
        logger.info("OpQuadPattern");
    }

    @Override public void visit(OpQuadBlock quadBlock)          {
        logger.info("quadBlock");
    }

    @Override public void visit(OpTriple opTriple)              {
        logger.info("OpTriple");
    }

    @Override public void visit(OpQuad opQuad)                  {
        logger.info("OpQuad");
    }

    @Override public void visit(OpPath opPath)                  {
        logger.info("OpPath");
    }

    @Override public void visit(OpProcedure opProc)             {
        logger.info("OpProcedure");
    }

    @Override public void visit(OpPropFunc opPropFunc)          {
        logger.info("OpPropFunc");
    }

    @Override public void visit(OpJoin opJoin)                  {
        logger.info("OpJoin");
    }

    @Override public void visit(OpSequence opSequence)          {
        logger.info("OpSequence");
    }

    @Override public void visit(OpDisjunction opDisjunction)    {
        logger.info("OpDisjunction");
    }

    @Override public void visit(OpLeftJoin opLeftJoin)          {
        logger.info("OpLeftJoin");
    }

    @Override public void visit(OpConditional opCond)           {logger.info("OpConditional");}

    @Override public void visit(OpMinus opMinus)                {logger.info("OpMinus");}

    @Override public void visit(OpDiff opDiff)                  {logger.info("OpDiff");}

    @Override public void visit(OpFilter opFilter)              {logger.info("OpFilter");}

    @Override public void visit(OpGraph opGraph)                {
        System.out.println("OpGraph");
    }

    @Override public void visit(OpService opService)            {logger.info("OpService");}

    @Override public void visit(OpDatasetNames dsNames)         {logger.info("OpDatasetNames");}

    @Override public void visit(OpTable opTable)                {logger.info("OpTable");}

    @Override public void visit(OpNull opNull)                  {logger.info("OpNull");}

    @Override public void visit(OpLabel opLabel)                {logger.info("OpLabel");}

    @Override public void visit(OpAssign opAssign)              {logger.info("OpAssign");}

    @Override public void visit(OpExtend opExtend)              {
        logger.info("OpExtend " + opExtend.getVarExprList().getVars().get(0).getVarName());
        String varName = opExtend.getVarExprList().getVars().get(0).getVarName();
        VarResult var = varResults.get(varID);
        var.key = varName;
        var.traversal += ".as('" + varName + "')";
        varResults.set(varID, var);
        varID ++;
    }

    @Override public void visit(OpList opList)                  {
        logger.info("OpList ");
    }

    @Override public void visit(OpOrder opOrder)                {
        logger.info("OpOrder " + opOrder.getConditions().get(0).getExpression().getVarName());
        String key = opOrder.getConditions().get(0).getExpression().getVarName();
        PatternNode node = graph.nodes.Get(key);
        if(node.Property()) {
            String predict = graph.edges.PropertyEdge(node.Name()).Label();
            orderByStr = ".select('" + graph.edges.PropertyEdge(node.Name()).OutNode() + "').order().by(" + predict + ")";
        }
        else {
            String predict = "vertexID";
            orderByStr = ".select('"+node.Name()+"').order().by('" + predict + "')";
        }
    }

    @Override public void visit(OpProject opProject)            {
        logger.info("OpProject " + opProject.getVars());
    }

    @Override public void visit(OpReduced opReduced)            {
        logger.info("visit OpReduced");
    }

    @Override public void visit(OpSlice opSlice)                {
        logger.info("OpSlice ");
        Long end = opSlice.getStart() + opSlice.getLength();
        logger.info("OpSlice " + opSlice.getStart() + "-" + Short.valueOf(end.toString()));
        logger.info(".range(" + opSlice.getStart() + ", " + end + ")");
        rangeStr = ".range("+opSlice.getStart() +", " + end +")";
    }

    @Override public void visit(OpGroup opGroup)                {
        logger.info("OpGroup ");
        if(opGroup.getAggregators().isEmpty()) return;
        int cntK = 0;
        for(ExprAggregator exprAgg: opGroup.getAggregators()) {
            logger.info("OpGroup " + exprAgg);
            ExprParser exprParser = new ExprParser();
            exprAgg.visit(exprParser);
            VarResult res = new VarResult(exprParser.key, exprParser.traversal);
            varResults.add(cntK,res);
            cntK ++;
        }
    }

    @Override public void visit(OpTopN opTop)                   {
        logger.info("OpTopN");
    }
}