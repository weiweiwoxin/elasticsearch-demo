package com.example.elasticsearchdemo.engine.client;

import com.example.elasticsearchdemo.config.MainConfiguration;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.jboss.logging.Logger;

import javax.security.auth.login.Configuration;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.Set;

/**
 * @Description: Elastic客户端
 * @author: jlsong E-mail: rainmap@163.com
 * @date: created in 上午10:34 19-7-1
 */
public class ElasticsearchClient {

    private static final Logger logger = Logger.getLogger(ElasticsearchClient.class);


    public static TransportClient getClient(String clusterName) {
        TransportClient client = null;
        try {
            logger.info("Starting getting es client.");

            Settings.Builder settings = Settings.builder();
            settings.put("transport.tcp.compress", true);
            settings.put("client.transport.sniff", true);
            settings.put("client.name", clusterName);

            Set<String> servers = MainConfiguration.validServers;
            if (0 == servers.size()) {
                throw new Exception(String.format("Elastic server info is null"));
            }

            TransportAddress[] addresses = new TransportAddress[servers.size()];
            Iterator<String> iterator = servers.iterator();
            for (int i = 0; iterator.hasNext(); i++) {
                String server = iterator.next();
                addresses[i] = new TransportAddress(InetAddress.getByName(server), MainConfiguration.validPort);
            }

            client = new PreBuiltXPackTransportClient(settings.build());
            client.addTransportAddresses(addresses);

            logger.info("getting es client successful");

        } catch (Exception e) {
            close(client);
            logger.fatal("Unable to get es connection", e);
            Runtime.getRuntime().halt(-1);
        }
        return client;
    }



    public static void close(TransportClient client){
        try {
            if(null != client){
                client.close();
            }
        }catch (Exception e){
            final String msg = String.format("Failed with a [%s] when close elasticsearch connection", e.getMessage());
            logger.error(msg, e);
        }
    }


    /**
     * 获取es客户端
     *
     * @return
     */
//    public static RestHighLevelClient getConnection(){
//        try {
//            ServerData[] esCluster = ConfigServer.getServer("APPENGINE");
//            HttpHost[] hosts = new HttpHost[esCluster.length];
//            for (int i =0; i != esCluster.length; i++){
//                hosts[i] = new HttpHost(InetAddress.getByName(esCluster[i].ibIp), 9300);
//            }
//            return new RestHighLevelClient(RestClient.builder(hosts));
//        }catch (Exception e){
//            Runtime.getRuntime().halt(-1);
//        }
//        return null;
//    }



}
