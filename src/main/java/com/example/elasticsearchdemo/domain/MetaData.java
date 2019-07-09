package com.example.elasticsearchdemo.domain;

import com.example.elasticsearchdemo.utils.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;


/**
 * @Description: TODO 文档元数据
 * @author: jlsong E-mail: rainmap@163.com
 * @date: created in 下午2:27 19-7-1
 */
public class MetaData {

    private static Logger logger = Logger.getLogger(MetaData.class);

    private String indexName;
    private String dataType;
    private String requestId;
    private String jsonEntity;
    private Object entity;

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public MetaData(String indexName, String dataType, String requestId, String jsonEntity) {
        this.indexName = indexName;
        this.dataType = dataType;
        this.requestId = requestId;
        this.jsonEntity = jsonEntity;
    }

    public MetaData(String indexName, String dataType, String requestId, Object entity) {
        this.indexName = indexName;
        this.dataType = dataType;
        this.requestId = requestId;
        this.entity = entity;
    }

    public void preParserEntity(){
        String json = null;
        try {
            json = OBJECT_MAPPER.writeValueAsString(entity);
            jsonEntity = StringUtils.prettyJsonString(json);
        } catch (JsonProcessingException e) {
           String msg = String.format("Entity transfer to json String failed,the reason is %s", e.getMessage());
           logger.error(msg, e);
           jsonEntity = "";
        }

    }

    public String getIndexName() {
        return indexName;
    }

    public String getDataType() {
        return dataType;
    }

    public String getRequestId() {
        if (null == requestId){
            requestId = String.format("%s", System.currentTimeMillis());
        }
        return requestId;
    }

    public String getJsonEntity() {
        return jsonEntity;
    }
}
