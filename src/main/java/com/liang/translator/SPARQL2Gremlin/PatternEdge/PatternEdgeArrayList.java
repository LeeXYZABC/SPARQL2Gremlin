package com.liang.translator.SPARQL2Gremlin.PatternEdge;

import java.util.ArrayList;

/**
 * Created by cnic-liliang on 2017/7/24.
 */
public class PatternEdgeArrayList {
    ArrayList<PatternEdge>edges = new ArrayList<PatternEdge>();

    public Integer Add(PatternEdge edge) {
        edges.add(edge);
        return edges.indexOf(edge);
    }
    public int Index(PatternEdge edge) {
        return 0;
    }
    public Boolean Contain(PatternEdge e) {
        for(PatternEdge edge: edges) {
            if(edge.Equel(e)) return true;
        }
        return false;
    }
    public PatternEdge PropertyEdge(String property){
        for(PatternEdge pe: edges) {
            if(pe.inNode.equals(property)) return pe;
        }
        return null;
    }

    public ArrayList<PatternEdge> Edges(){
        return edges;
    }

    public PatternEdge GetEdge(String node1, String node2) {
        for(PatternEdge e: edges) {
            if(e.inNode.contentEquals(node1) && e.outNode.contentEquals(node2))
                return e;
            if(e.inNode.contentEquals(node2) && e.outNode.contentEquals(node1))
                return e;
        }
        return null;
    }

    public ArrayList<PatternEdge> AvailableEdges() {
        ArrayList<PatternEdge> arrList = new ArrayList<PatternEdge>();
        for(PatternEdge e: Edges()) {
            if(e.hasTraversed == false) arrList.add(e);
        }
        return arrList;
    }

    public void SetTraversed(PatternEdge e){
        if(edges.contains(e) == false) return;
        Integer index = edges.indexOf(e);
        e.hasTraversed = true;
        edges.set(index, e);
    }
}
