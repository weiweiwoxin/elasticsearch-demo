package com.example.elasticsearchdemo.service;

import com.example.elasticsearchdemo.domain.ConditionBean;
import com.example.elasticsearchdemo.domain.MetaData;
import com.example.elasticsearchdemo.engine.SearchEngineService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: Elastic服务实现
 * @author: jlsong E-mail: rainmap@163.com
 * @date: created in 上午10:32 19-7-1
 */
@Service
public class DemoServiceImpl implements DemoService {
    @Override
    public String createDoc(MetaData data) {
        return SearchEngineService.getInstance().createIndex(data);
    }

    @Override
    public String getDoc(MetaData data) {
        return SearchEngineService.getInstance().getIndex(data);
    }

    @Override
    public String deleteDoc(MetaData data) {
        return SearchEngineService.getInstance().deleteIndex(data);
    }

    @Override
    public String updateDoc(MetaData data) {
        return SearchEngineService.getInstance().updateIndex(data);
    }

    @Override
    public String getDocByCondition(MetaData data, boolean isAllQuery, String keyword, String... fieldName) {
        return SearchEngineService.getInstance().getIndexByCondition(data, isAllQuery, keyword, fieldName);
    }

    @Override
    public String wildDocByCondition(MetaData data, String keyword, String fieldName) {
        Map<String, String> condition = new HashMap<>(16);
        condition.put(fieldName, keyword);
        return SearchEngineService.getInstance().getIndexByWildCondition(data, condition);
    }

    @Override
    public String getDocByComplexCondition(MetaData data, List<ConditionBean> conditions) {
        return SearchEngineService.getInstance().complexGetIndexByCondition(data, conditions);
    }
}
