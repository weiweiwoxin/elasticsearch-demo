package com.example.elasticsearchdemo.service;

import com.example.elasticsearchdemo.domain.MetaData;
import com.example.elasticsearchdemo.engine.SearchEngineService;

/**
 * @Description: Elastic服务实现
 * @author: jlsong E-mail: rainmap@163.com
 * @date: created in 上午10:32 19-7-1
 */
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
}
