package com.liang.translator.SPARQL2Gremlin;

import org.apache.jena.sparql.expr.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cnic-liliang on 2017/7/27.
 */
public class ExprParser extends ExprVisitorBase {
    String traversal = "";
    String key = "";
    private static Logger logger = LoggerFactory.getLogger(ExprParser.class);

    @Override
    public void visit(ExprAggregator agg)    {
        String aggName = agg.getAggregator().getName();
        String aggStr = agg.getAggregator().key();
        ExprParser subExprParser = new ExprParser();
        agg.getAggregator().getExprList().getList().get(0).visit(subExprParser);
        //select
        traversal += ".select('" + subExprParser.traversal + "')";
        //dedup if needed
        if(aggStr.toLowerCase().contains("distinct")) {
            traversal += ".dedup()";
        }
        //aggregate operation
        switch (aggName.toLowerCase()) {
            case "count":
                traversal += ".count()";
                key = subExprParser.traversal;
            default: ;
        }
    }

    @Override
    public void visit(NodeValue nv)         {
        logger.info("NodeValue " + nv);
    }

    @Override
    public void visit(ExprVar nv)           {
        logger.info("ExprVar " + nv);
        traversal = nv.getVarName();
    }

    @Override
    public void visit(ExprFunctionOp op)    {
        logger.info("ExprFunctionOp");
    }

    @Override
    public void visit(ExprFunction0 func)   {
        logger.info("ExprFunction0");
    }
}
