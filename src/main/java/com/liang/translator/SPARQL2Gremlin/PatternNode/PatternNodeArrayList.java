package com.liang.translator.SPARQL2Gremlin.PatternNode;

import com.liang.translator.SPARQL2Gremlin.PatternEdge.PatternEdge;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cnic-liliang on 2017/7/24.
 */
public class PatternNodeArrayList {
    public HashMap<String, PatternNode> nodes = new HashMap<String, PatternNode>();

    public ArrayList<PatternNode> AvailableNodeList(){
        ArrayList<PatternNode> arrList = new ArrayList<PatternNode>();
        for(PatternNode n: nodes.values()) {
            if(n.isVar == false && n.isProperty == false)
                arrList.add(n);
        }
        return arrList;
    }

    public HashMap<String, PatternNode> Nodes() {
        return nodes;
    }
    public Boolean NodesContain(String name) {
        return nodes.containsKey(name);
    }
    public void NodePut(PatternNode n) {
        nodes.put(n.name, n);
    }

    public PatternNode Get(String name) {
        return nodes.get(name);
    }
    public Boolean Contain(PatternNode n) {
        return nodes.containsKey(n.name);
    }
    public void Update(PatternEdge e) {
        String in = e.InNode();
        String out = e.OutNode();
        if(nodes.get(in).in.contains(e) == false) {
            PatternNode node = nodes.get(in);
            node.in.add(e);
            nodes.put(node.Name(), node);
        }

        if(nodes.get(out).out.contains(e) == false){
            PatternNode node = nodes.get(out);
            node.out.add(e);
            nodes.put(node.Name(), node);
        }
        if(e.IsProperty()) {
            PatternNode pn = nodes.get(e.InNode());
            pn.SetProperty();
            nodes.put(e.InNode(), pn);
        }
    }

    public void Merge(PatternNodeArrayList cns) {
        for(PatternNode n: cns.Nodes().values()) {
            if(nodes.containsKey(n.name) == false) nodes.put(n.name, n);
        }
    }
}
