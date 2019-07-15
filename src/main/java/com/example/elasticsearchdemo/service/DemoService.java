package com.example.elasticsearchdemo.service;

import com.example.elasticsearchdemo.domain.ConditionBean;
import com.example.elasticsearchdemo.domain.MetaData;

import java.util.List;

/**
 * @Description: Elastic服务接口
 * @author: jlsong E-mail: rainmap@163.com
 * @date: created in 上午10:32 19-7-1
 */
public interface DemoService {

    /**
     * 创建Es文档
     * @param data
     * @return
     */
    String createDoc(MetaData data);

    /**
     * 查询Es文档
     *
     * @param data
     * @return
     */
    String getDoc(MetaData data);

    /**
     * 删除Es文档
     *
     * @param data
     * @return
     */
    String deleteDoc(MetaData data);

    /**
     * 修改Es文档
     *
     * @param data
     * @return
     */
    String updateDoc(MetaData data);


    String getDocByCondition(MetaData data, boolean isAllQuery, String keyword, String... fieldName);

    String wildDocByCondition(MetaData data, String keyword, String fieldName);

    String getDocByComplexCondition(MetaData data, List<ConditionBean> conditions);
}
