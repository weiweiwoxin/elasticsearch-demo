package com.example.elasticsearchdemo;

import com.example.elasticsearchdemo.domain.ConditionBean;
import com.example.elasticsearchdemo.domain.MetaData;
import com.example.elasticsearchdemo.service.DemoService;
import com.example.elasticsearchdemo.vo.User;
import org.jboss.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticsearchDemoApplicationTests {

    private static final Logger logger = Logger.getLogger(ElasticsearchDemoApplicationTests.class);

    @Test
    public void contextLoads() {
    }

    @Autowired
    DemoService demoService;

    @Test
    public void addData(){
        long base = System.currentTimeMillis();
        User user = new User();
        user.setName("wusangui");
        user.setPassword("12##56");
        user.setNickname("吴三桂的高圆圆");
        user.setEmail("wusangui@gmail.com");
        LocalDateTime now = LocalDateTime.now();
        user.setUpdateTime(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        user.setCreateTime(now.getSecond());

        MetaData data = new MetaData("estest", "user", null, user);
        String result = demoService.createDoc(data);
        logger.debug(String.format("elasticsearch add index take time [%s]ms", System.currentTimeMillis() - base));
        System.out.println(result);

    }

    @Test
    public void getData(){
        long base = System.currentTimeMillis();
        String id = "1563181125580";
        MetaData data = new MetaData("estest", "user", id, "");
        String result = demoService.getDoc(data);
        logger.debug(String.format("elasticsearch get index take time [%s]ms", System.currentTimeMillis() - base));
        System.out.println(result);
    }


    @Test
    public void updateData(){
        long base = System.currentTimeMillis();
        String id = "";
        User user = new User();
        user.setNickname("吴三桂改");
        MetaData data = new MetaData("estest", "user", id, user);
        String result = demoService.updateDoc(data);
        logger.debug(String.format("elasticsearch update index take time [%s]ms", System.currentTimeMillis() - base));
        System.out.println(result);
    }

    @Test
    public void deleteData(){
        long base = System.currentTimeMillis();
        String id = "";
        MetaData data = new MetaData("estest", "user", id, "");
        String result = demoService.deleteDoc(data);
        logger.debug(String.format("elasticsearch delete index take time [%s]ms", System.currentTimeMillis() - base));
        System.out.println(result);
    }

    /**
     * 查询自定义条件文档记录
     */
    @Test
    public void getCustomData(){
        long base = System.currentTimeMillis();
        MetaData data = new MetaData("estest", "user", "", "");

        String result = demoService.getDocByCondition(data, false, "wusangui", "name");
        logger.debug(String.format("elasticsearch get index by custom condition take time [%s]ms", System.currentTimeMillis() - base));
        System.out.println(result);
    }

    /**
     * 模糊查询
     */
    @Test
    public void getWildData(){
        long base = System.currentTimeMillis();
        MetaData data = new MetaData("estest", "user", "", "");

        String result = demoService.wildDocByCondition(data,  "wusan", "name");
        logger.debug(String.format("elasticsearch get index by wild condition take time [%s]ms", System.currentTimeMillis() - base));
        System.out.println(result);
    }


    /**
     * 复合条件下查询
     */
    @Test
    public void boolGetData(){
        long base = System.currentTimeMillis();
        MetaData data = new MetaData("estest", "user", "", "");

        List<ConditionBean> conditionBeans = new ArrayList<>();
        ConditionBean conditionBean = new ConditionBean();
        conditionBean.setFieldName("name");
        conditionBean.setKeywords(new String[]{"wusangui"});
        conditionBean.setQueryType(ConditionBean.QueryTypeEnum.SHOULD);
        conditionBean.setBoolType(ConditionBean.BoolTypeEnum.TERM);
        conditionBeans.add(conditionBean);

        String result = demoService.getDocByComplexCondition(data, conditionBeans);
        logger.debug(String.format("elasticsearch get index by complex condition take time [%s]ms", System.currentTimeMillis() - base));
        System.out.println(result);
    }

    /**
     * 复合条件下查询
     */
    @Test
    public void boolGetData2(){
        long base = System.currentTimeMillis();
        MetaData data = new MetaData("estest", "user", "", "");

        List<ConditionBean> conditionBeans = new ArrayList<>();
        ConditionBean conditionBean = new ConditionBean();
        conditionBean.setFieldName("name");
        conditionBean.setKeywords(new String[]{"wusangui", "liuzhizhi"});
        conditionBean.setQueryType(ConditionBean.QueryTypeEnum.SHOULD);
        conditionBean.setBoolType(ConditionBean.BoolTypeEnum.TERMS);
        conditionBeans.add(conditionBean);

        String result = demoService.getDocByComplexCondition(data, conditionBeans);
        logger.debug(String.format("elasticsearch get index by complex condition take time [%s]ms", System.currentTimeMillis() - base));
        System.out.println(result);
    }




}
