package com.galen.program.netty.httpProxy;

import com.galen.program.netty.client.DefaultTcpClientConfig;
import com.galen.program.netty.client.TcpClientConfig;
import com.galen.program.netty.client.sync.DefaultSyncTcpClient;
import com.galen.program.netty.client.sync.DefaultSyncTcpPoolClient;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

/**
 * Created by baogen.zhang on 2018/11/8
 *
 * @author baogen.zhang
 * @date 2018/11/8
 */
public class HttpProxyClient {
    private DefaultSyncTcpPoolClient<FullHttpResponse> channel;

    public HttpProxyClient(String ip, int port) {
        this(new GenericKeyedObjectPoolConfig(), ip, port);
    }

    public HttpProxyClient(GenericKeyedObjectPoolConfig poolConfig, String ip, int port) {
        DefaultTcpClientConfig channelConfig = new DefaultTcpClientConfig();
        channelConfig.setIp(ip);
        channelConfig.setPort(port);

        channelConfig.addHandler(HttpClientCodec.class);
        channelConfig.getHandlerFactories().add(new TcpClientConfig.ChannelHandlerFactory() {
            @Override
            public ChannelHandler newChannelHandler() {
                return new HttpObjectAggregator(Integer.MAX_VALUE);
            }
        });
        channelConfig.addHandler(ChunkedWriteHandler.class);
        channelConfig.addHandler(new DefaultSyncTcpClient.SyncClientCompleteHandler<FullHttpResponse>());

        channel = new DefaultSyncTcpPoolClient<FullHttpResponse>(poolConfig, channelConfig);
    }

    void close() {
        channel.close();
    }

    /**
     * @param msg
     * @return
     * @throws Exception
     * @see #send(FullHttpRequest, long)
     */
    FullHttpResponse send(FullHttpRequest msg) throws Exception {
        return channel.send(msg);
    }

    /**
     * 发送请求消息，并等待回复消息收到后返回。或者超时抛出异常返回。
     * TcpClientConfig中要添加completeHandler，这里才能识别出回复消息received msg。
     *
     * @param msg  请求消息
     * @param time 超时时间，时间单位为毫秒
     * @return
     * @throws Exception
     */
    FullHttpResponse send(FullHttpRequest msg, long time) throws Exception {
        return channel.send(msg, time);
    }

}
