package com.galen.program.netty.httpProxy;

import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by baogen.zhang on 2018/11/9
 *
 * @author baogen.zhang
 * @date 2018/11/9
 */
public class HttpProxyClientManager {
    public static final ConcurrentHashMap<String, HttpProxyClient> serverClients = new ConcurrentHashMap<String, HttpProxyClient>();

    private HttpProxyClientManager() {
    }

    ;

    public static void register(String server, String ip, int port) {
        Assert.hasLength(server);
        Assert.hasLength(ip);
        Assert.isTrue(port > 0);
        if (!serverClients.contains(server)) {
            HttpProxyClient client = new HttpProxyClient(ip, port);
            serverClients.putIfAbsent(server, client);
        }
    }

    public static HttpProxyClient receive(String server) {
        Assert.hasLength(server);
        return serverClients.get(server);
    }

    public static void unregister(String server) {
        Assert.hasLength(server);
        HttpProxyClient client = serverClients.remove(server);
        if (client != null) {
            client.close();
        }
    }
}
