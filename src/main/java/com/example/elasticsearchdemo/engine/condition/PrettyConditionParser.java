package com.example.elasticsearchdemo.engine.condition;

import org.elasticsearch.index.query.BoolQueryBuilder;

/**
 * @Description: TODO
 * @author: jlsong E-mail: rainmap@163.com
 * @date: created in 下午4:46 19-7-1
 */
public class PrettyConditionParser {


    private String requestId;
    private String[] dataType;


    private String orgCondition;


    private String queryConditionStr;

    private QueryCondition queryCondition;

    private BoolQueryBuilder boolQueryBuilder;

    public PrettyConditionParser(String orgCondition) {
        this.orgCondition = orgCondition;
        this.requestId = String.format("%s", System.currentTimeMillis());
    }


    private void queryConditionParser(){
        if (queryCondition != null){
            dataType = queryCondition.dataType.split(",");


        }
    }
}
