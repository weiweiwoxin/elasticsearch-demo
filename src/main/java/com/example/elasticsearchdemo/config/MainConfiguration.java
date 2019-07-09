package com.example.elasticsearchdemo.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description: TODO
 * @author: jlsong E-mail: rainmap@163.com
 * @date: created in 上午10:24 19-7-1
 */
@ComponentScan(basePackages = {"com.example.elasticsearchdemo"})
@Configuration
public class MainConfiguration implements InitializingBean {

    @Value("${es.config.server.port}")
    public Integer port;

    @Value("${es.config.server.ip}")
    public String server;

    @Value("${es.config.server.cluster}")
    public String cluster;

    /**
     * 集群
     */
    public static Set<String> validClusters = new HashSet<>(16);

    /**
     * 服务器群
     */
    public static Set<String> validServers = new HashSet<>(16);

    /**
     * 端口
     */
    public static int validPort = 0;

    @Override
    public void afterPropertiesSet() throws Exception {
        validClusters.addAll(Arrays.asList(cluster.split(",")));
        validServers.addAll(Arrays.asList(server.split(",")));
        validPort=port;
    }
}
