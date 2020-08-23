package com.ssrs.elasticsearch.service;

import cn.hutool.core.util.StrUtil;
import com.ssrs.elasticsearch.config.EsServerConfig;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

/**
 * ES Rest client 服务
 */
public class ClientService {

    RestClient client = null;

    public ClientService() {
        String esServer = EsServerConfig.getValue();
        if (StrUtil.isEmpty(esServer)) {
            throw new RuntimeException("es server config can not be null!");
        }
        String[] servers = esServer.split(";");
        HttpHost[] hosts = new HttpHost[servers.length];
        for (int i = 0; i < servers.length; i++) {
            String server = servers[i];
            String domain = server.split(":")[0];
            String port = server.split(":")[1];
            hosts[i] = new HttpHost(domain, Integer.parseInt(port), "http");
        }
        client = RestClient.builder(hosts).build();
    }

    public RestClient getClient() {
        return client;
    }
}
