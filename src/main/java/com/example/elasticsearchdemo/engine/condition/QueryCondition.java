package com.example.elasticsearchdemo.engine.condition;

import java.util.Arrays;

/**
 * @Description: TODO 条件查询
 * @author: jlsong E-mail: rainmap@163.com
 * @date: created in 下午4:47 19-7-1
 */
public class QueryCondition {

    public String requestId;
    public String dataType;
    public String expressionStr;
    public ParamCondition[] paramConditions;

    public QueryCondition(String requestId, String dataType, String expressionStr, ParamCondition[] paramConditions) {
        this.requestId = requestId;
        this.dataType = dataType;
        this.expressionStr = expressionStr;
        this.paramConditions = paramConditions;
    }

    @Override
    public String toString() {
        return "QueryCondition{" +
                "requestId='" + requestId + '\'' +
                ", dataType='" + dataType + '\'' +
                ", expressionStr='" + expressionStr + '\'' +
                ", paramConditions=" + Arrays.toString(paramConditions) +
                '}';
    }

    public static class ParamCondition{
        public String fieldName;
        public String keyword;
        public String demandType;

        public ParamCondition(String fieldName, String keyword, String demandType) {
            this.fieldName = fieldName;
            this.keyword = keyword;
            this.demandType = demandType;
        }

        @Override
        public String toString() {
            return "ParamCondition{" +
                    "fieldName='" + fieldName + '\'' +
                    ", keyword='" + keyword + '\'' +
                    ", demandType='" + demandType + '\'' +
                    '}';
        }
    }
}
