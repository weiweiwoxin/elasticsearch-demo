//package com.example.elasticsearchdemo.engine.search;
//
//import com.example.elasticsearchdemo.engine.condition.QueryCondition;
//
///**
// * @Description: TODO
// * @author: jlsong E-mail: rainmap@163.com
// * @date: created in 下午4:58 19-7-1
// */
//public class SearchConditionParser {
//
//    public static final String CAP_TIME_FIELD = "CapTime";
//    public static final String CAP_TIME_FACKED = "__CapTime__";
//
//    public final QueryCondition queryCondition;
//    public final SearchCondition searchCondition;
//
//    public SearchConditionParser(QueryCondition queryCondition) {
//        // 符合转换成Es可识别
//        this.queryCondition = DataFormatCoverter.optimizeQuery(queryCondition);
//        searchCondition = new SearchCondition();
//        searchCondition
//    }
//
//
//    public int parserQueryRequest(){
//        try {
//            String suffixLogicExpr = LogicExpressionUtils.suffixExpression(queryCondition.expressionStr);
//            if (suffixLogicExpr == null){
//                sea
//            }
//        }catch (Exception e){
//
//        }
//    }
//}
