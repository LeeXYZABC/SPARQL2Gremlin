package com.liang.translator.SPARQL2Gremlin.PatternEdge;

/**
 * Created by cnic-liliang on 2017/7/24.
 */

public class PatternEdge {
    String label;
    Boolean isProperty;
    Boolean hasTraversed = false;
    String inNode;
    String outNode;

    public PatternEdge(String clabel, Boolean cisProperty, String coutNode, String cinNode) {
        label = clabel;
        isProperty = cisProperty;
        inNode = cinNode;
        outNode = coutNode;
    }

    public Boolean Equel(PatternEdge edge) {
        if(edge.label.contentEquals(label) && edge.isProperty == isProperty
                && edge.inNode.contentEquals(inNode) && edge.outNode.contentEquals(outNode))
            return true;
        return false;
    }

    public String Label(){
        return label;
    }
    public Boolean IsProperty(){
        return isProperty;
    }
    public Boolean HasTraversed() {
        return hasTraversed;
    }
    public String InNode(){
        return inNode;
    }
    public String OutNode(){
        return outNode;
    }
}
