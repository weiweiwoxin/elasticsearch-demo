package com.example.elasticsearchdemo.engine;

import com.example.elasticsearchdemo.config.MainConfiguration;
import com.example.elasticsearchdemo.domain.ConditionBean;
import com.example.elasticsearchdemo.domain.MetaData;
import com.example.elasticsearchdemo.engine.client.ElasticsearchClient;
import com.example.elasticsearchdemo.result.ResponseResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.concurrent.locks.Condition;

/**
 * @Description: Elastic搜索主服务
 * @author: jlsong E-mail: rainmap@163.com
 * @date: created in 上午10:31 19-7-1
 */
public class SearchEngineService {

    private static final Logger logger = Logger.getLogger(SearchEngineService.class);

    private Map<String, TransportClient> clients;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static volatile SearchEngineService instance;

    private SearchEngineService(){
        this.clients = new HashMap<>(16);
    }

    /**
     * 单例设计模式
     * @return
     */
    public static SearchEngineService getInstance(){
        if (instance == null){
            synchronized (SearchEngineService.class){
                if (instance == null){
                    instance = new SearchEngineService();
                }
            }

        }
        return instance;
    }


    /**
     * 挑选缓存里的es客户端
     *
     * @param roleName
     * @return
     */
    private TransportClient getEsClient(String roleName){
        TransportClient client;
        if (clients.containsKey(roleName)){
            client = clients.get(roleName);
        }else {
            client = ElasticsearchClient.getClient(roleName);
            clients.putIfAbsent(roleName, client);
        }
        return client;
    }


    public String executeElasticSearchKernel(String clusterName, List<String> indexName, MetaData data){
        ResponseResult result = null;
        String requestId = data.getRequestId();
        try {
            TransportClient client = getEsClient(clusterName);
            SearchRequestBuilder requestBuilder = client.prepareSearch(indexName.toArray(new String[]{}))
                    .setIndicesOptions(IndicesOptions.lenientExpandOpen());
            QueryBuilder queryBuilder = QueryBuilders.matchQuery("name", "1");
            AggregationBuilder aggregationBuilder = AggregationBuilders.range("");
            requestBuilder.addAggregation(aggregationBuilder);
            requestBuilder.setQuery(queryBuilder);


            SearchResponse response = requestBuilder.get();

        }catch (Exception e){
            result = new ResponseResult(requestId, -2, e.getMessage() +", " + e.getCause(), "[]");
            logger.error(String.format("Es doc request[%s], execute createIndex failed", requestId), e);
        }

        logger.info(String.format("Es doc request[%s], result <%s>", requestId, null == result ? "" : result.toString()));


        return null == result ? "" : result.toString();

    }




    public String createIndex(MetaData metaData){
        ResponseResult result = null;
        String requestId = metaData.getRequestId();
        String clusterName = getClusterName(MainConfiguration.validClusters);

        try {
            TransportClient client = getEsClient(clusterName);
            IndexResponse response = client.prepareIndex(metaData.getIndexName(), metaData.getDataType(), requestId)
                    .setSource(metaData.getJsonEntity(), XContentType.JSON)
                    .get();

            result = new ResponseResult(requestId, 0, "", response.toString());
        }catch (Exception e){
            result = new ResponseResult(requestId, -2, e.getMessage() +", " + e.getCause(), "[]");
            logger.error(String.format("Es doc request[%s], execute createIndex failed", requestId), e);
        }

        logger.info(String.format("Es doc request[%s], result <%s>", requestId, null == result ? "" : result.toString()));

        return null == result ? "" : result.toString();
    }


    /**
     * 获取Es文档记录
     *
     * @param metaData
     * @return
     */
    public String getIndex(MetaData metaData){
        ResponseResult result = null;
        String requestId = metaData.getRequestId();
        String clusterName = getClusterName(MainConfiguration.validClusters);

        try {
            TransportClient client = getEsClient(clusterName);
            GetResponse response = client.prepareGet(metaData.getIndexName(), metaData.getDataType(), requestId)
                    .get();

            result = new ResponseResult(requestId, 0, "", response.getSourceAsString());
        }catch (Exception e){
            result = new ResponseResult(requestId, -2, e.getMessage() +", " + e.getCause(), "[]");
            logger.error(String.format("Es doc request[%s], execute getIndex failed", requestId), e);
        }

        logger.info(String.format("Es doc request[%s], result <%s>", requestId, null == result ? "" : result.toString()));

        return null == result ? "" : result.toString();
    }


    public String complexGetIndexByCondition(MetaData metaData, List<ConditionBean> conditions){
        ResponseResult result = null;
        String requestId = metaData.getRequestId();
        String clusterName = getClusterName(MainConfiguration.validClusters);

        BoolQueryBuilder boolQueryBuilder = parserQueryConditionBean(conditions);
        try {
            TransportClient client = getEsClient(clusterName);
            SearchRequestBuilder requestBuilder = client
                    .prepareSearch()
                    .setIndices(metaData.getIndexName())
                    .setTypes(metaData.getDataType())
                    .setQuery(boolQueryBuilder);

            SearchResponse response = requestBuilder.get();

            result = new ResponseResult(requestId, 0, "", response.toString());
        }catch (Exception e){
            result = new ResponseResult(requestId, -2, e.getMessage() +", " + e.getCause(), "[]");
            logger.error(String.format("Es doc request[%s], execute complexGetIndexByCondition failed", requestId), e);
        }

        logger.info(String.format("Es doc request[%s], result <%s>", requestId, null == result ? "" : result.toString()));

        return null == result ? "" : result.toString();


    }

    private BoolQueryBuilder parserQueryConditionBean(List<ConditionBean> conditions) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (ConditionBean condition : conditions){
            QueryBuilder qb = selectBoolBean(condition);
            addBoolQuery(qb, boolQueryBuilder, condition.getQueryType().toString());
        }
        return boolQueryBuilder;
    }

    private void addBoolQuery(QueryBuilder qb, BoolQueryBuilder boolQueryBuilder, String s) {
        if (qb == null){
            return;
        }
        switch (s){
            case "must":
                boolQueryBuilder.must(qb);
                break;
            case "must_not":
                boolQueryBuilder.mustNot(qb);
                break;
            case "should":
                boolQueryBuilder.should(qb);
                break;
            case "filter":
                boolQueryBuilder.filter(qb);
                break;
                default:
                    break;
        }
    }

    private QueryBuilder selectBoolBean(ConditionBean condition) {
        switch (condition.getBoolType().toString()){
            case "term":
                return QueryBuilders.termQuery(condition.getFieldName(), condition.getKeywords()[0]);
            case "terms":
                return QueryBuilders.termsQuery(condition.getFieldName(), condition.getKeywords());
            case "range":
                return QueryBuilders.rangeQuery(condition.getFieldName()).from(condition.getKeywords()[0]).to(condition.getKeywords()[1])
                        .includeLower(Boolean.valueOf(condition.getKeywords()[2]))
                        .includeUpper(Boolean.valueOf(condition.getKeywords()[3]));
                default:
                    return null;

        }
    }


    /**
     * 删除Es文档
     *
     * @param metaData
     * @return
     */
    public String deleteIndex(MetaData metaData){
        ResponseResult result = null;
        String requestId = metaData.getRequestId();
        String clusterName = getClusterName(MainConfiguration.validClusters);

        try {
            TransportClient client = getEsClient(clusterName);
            DeleteResponse response = client.prepareDelete(metaData.getIndexName(), metaData.getDataType(), requestId)
                    .get();

            result = new ResponseResult(requestId, 0, "", response.toString());
        }catch (Exception e){
            result = new ResponseResult(requestId, -2, e.getMessage() +", " + e.getCause(), "[]");
            logger.error(String.format("Es doc request[%s], execute deleteIndex failed", requestId), e);
        }

        logger.info(String.format("Es doc request[%s], result <%s>", requestId, null == result ? "" : result.toString()));

        return null == result ? "" : result.toString();
    }


    /**
     * 修改Es文档
     *
     * @param metaData
     * @return
     */
    public String updateIndex(MetaData metaData){
        ResponseResult result = null;
        String requestId = metaData.getRequestId();
        String clusterName = getClusterName(MainConfiguration.validClusters);

        try {
            TransportClient client = getEsClient(clusterName);
            UpdateResponse response = client.prepareUpdate(metaData.getIndexName(), metaData.getDataType(), requestId)
                    .setDoc(metaData.getJsonEntity(), XContentType.JSON)
                    .get();

            result = new ResponseResult(requestId, 0, "", response.toString());
        }catch (Exception e){
            result = new ResponseResult(requestId, -2, e.getMessage() +", " + e.getCause(), "[]");
            logger.error(String.format("Es doc request[%s], execute deleteIndex failed", requestId), e);
        }

        logger.info(String.format("Es doc request[%s], result <%s>", requestId, null == result ? "" : result.toString()));

        return null == result ? "" : result.toString();
    }


    /**
     * 综合条件查询
     *
     * @param metaData
     * @param isAllQuery
     * @param keyword
     * @param fieldName
     * @return
     */
    public String getIndexByCondition(MetaData metaData, boolean isAllQuery, String keyword, String... fieldName){
        ResponseResult result = null;
        String requestId = metaData.getRequestId();
        String clusterName = getClusterName(MainConfiguration.validClusters);
        QueryBuilder queryBuilder = null;
        if (isAllQuery){
            queryBuilder = QueryBuilders.matchAllQuery();
        }else {
            if (fieldName.length < 1){
                logger.debug(String.format("Search request is null"));
                result = new ResponseResult(String.format("%s", requestId), -2 , "", "");
                return null == result ? "" : result.toString();
            }

            if (fieldName.length == 1){
                queryBuilder = QueryBuilders.matchQuery(fieldName[0], keyword);
            }else {
                queryBuilder = QueryBuilders.multiMatchQuery(keyword, fieldName);
            }
        }

        try {
            TransportClient client = getEsClient(clusterName);
            SearchRequestBuilder requestBuilder = client
                    .prepareSearch()
                    .setIndices(metaData.getIndexName())
                    .setTypes(metaData.getDataType())
                    .setQuery(queryBuilder);

            SearchResponse response = requestBuilder.get();

            result = new ResponseResult(requestId, 0, "", response.toString());
        }catch (Exception e){
            result = new ResponseResult(requestId, -2, e.getMessage() +", " + e.getCause(), "[]");
            logger.error(String.format("Es doc request[%s], execute getIndexByCondition failed", requestId), e);
        }

        logger.info(String.format("Es doc request[%s], result <%s>", requestId, null == result ? "" : result.toString()));

        return null == result ? "" : result.toString();
    }


    /**
     * 综合模糊条件查询
     *
     * @param metaData
     * @param queryCondition
     * @return
     */
    public String getIndexByWildCondition(MetaData metaData, Map<String, String> queryCondition){
        ResponseResult result = null;
        String requestId = metaData.getRequestId();
        String clusterName = getClusterName(MainConfiguration.validClusters);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (Map.Entry<String, String> entry : queryCondition.entrySet()){
            String fieldName = entry.getKey();
            String keyword = entry.getValue();
            if (keyword.contains("*")){
                String msg = String.format("Search request[%s] contain str '*'", keyword);
                logger.debug(msg);
                result = new ResponseResult(String.format("%s", requestId), -2, msg, "");
                return null == result ? "" : result.toString();
            }
            keyword = String.format("*%s*", keyword);
            WildcardQueryBuilder queryBuilder = QueryBuilders.wildcardQuery(fieldName, keyword);
            boolQueryBuilder.should(queryBuilder);
        }

        try {
            TransportClient client = getEsClient(clusterName);
            SearchRequestBuilder requestBuilder = client
                    .prepareSearch()
                    .setIndices(metaData.getIndexName())
                    .setTypes(metaData.getDataType())
                    .setQuery(boolQueryBuilder);

            SearchResponse response = requestBuilder.get();

            result = new ResponseResult(requestId, 0, "", response.toString());
        }catch (Exception e){
            result = new ResponseResult(requestId, -2, e.getMessage() +", " + e.getCause(), "[]");
            logger.error(String.format("Es doc request[%s], execute getIndexByWildCondition failed", requestId), e);
        }

        logger.info(String.format("Es doc request[%s], result <%s>", requestId, null == result ? "" : result.toString()));

        return null == result ? "" : result.toString();
    }


    /**
     * 获取Es服务器
     *
     * @param clusters
     * @return
     */
    public String getClusterName(Set<String> clusters){
        Iterator<String> iterator = clusters.iterator();
        while (iterator.hasNext()){
            return iterator.next();
        }
        return "";
    }
}
