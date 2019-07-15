//package com.example.elasticsearchdemo.engine.search;
//
//import com.example.elasticsearchdemo.engine.condition.QueryCondition;
//
///**
// * @Description: TODO
// * @author: jlsong E-mail: rainmap@163.com
// * @date: created in 下午5:05 19-7-1
// */
//public class DataFormatCoverter {
//
//    public static QueryCondition optimizeQuery(QueryCondition queryCondition){
//        for (QueryCondition.ParamCondition condition : queryCondition.paramConditions){
//            if ("-bt".equalsIgnoreCase(condition.demandType) || "-ot".equalsIgnoreCase(condition.demandType)){
//                String[] keywords = condition.keyword.split(",", 2);
//
//                if (keywords.length != 2 || keywords[0].equalsIgnoreCase(keywords[1])){
//                    condition.demandType = "-bt".equalsIgnoreCase(condition.demandType) ? "-eq" : "-ne";
//                    condition.keyword = keywords[0];
//                    continue;
//                }
//
//                if (condition.fieldName.equalsIgnoreCase(SearchConditionParser.CAP_TIME_FIELD) &&
//                        keywords[0].endsWith("000000") && keywords[1].endsWith("235959")){
//                    condition.fieldName = SearchConditionParser.CAP_TIME_FACKED;
//                }
//
//                continue;
//            }
//
//            if ("-in".equalsIgnoreCase(condition.demandType) || "-ni".equalsIgnoreCase(condition.demandType)){
//                if (condition.keyword.indexOf(",") == -1){
//                    condition.demandType = "-in".equalsIgnoreCase(condition.demandType) ? "-eq" : "-ne";
//                }
//            }
//
//        }
//        return queryCondition;
//    }
//}
