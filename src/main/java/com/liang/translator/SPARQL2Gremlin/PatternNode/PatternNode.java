package com.liang.translator.SPARQL2Gremlin.PatternNode;

import com.liang.translator.SPARQL2Gremlin.PatternEdge.PatternEdge;

import java.util.HashSet;
/**
 * Created by cnic-liliang on 2017/7/24.
 */

//Can be variable or fixed node in graph
public class PatternNode implements Cloneable{
    Boolean isVar = false;
    Boolean isProperty = false;
    String name;
    HashSet<PatternEdge> out = new HashSet<PatternEdge>();
    HashSet<PatternEdge> in = new HashSet<PatternEdge>();

    public PatternNode(Boolean cisVar, String cname) {
        isVar = cisVar;
        name = cname;

    }

    public void SetProperty() {
        isProperty = true;
    }

    public Boolean Property() {
        return isProperty;
    }

    public String Name() {
        return name;
    }
    public HashSet<PatternEdge> Out() {
        return out;
    }
    public HashSet<PatternEdge> In() {
        return in;
    }
    public  Boolean IsVar(){
        return isVar;
    }
}
