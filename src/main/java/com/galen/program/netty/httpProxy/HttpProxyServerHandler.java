package com.galen.program.netty.httpProxy;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by baogen.zhang on 2018/11/8
 *
 * @author baogen.zhang
 * @date 2018/11/8
 */
public class HttpProxyServerHandler extends ChannelHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(HttpProxyServerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //   HttpProxyClientManager.register("ext","127.0.0.1",8088);
        HttpProxyClientManager.register("xir", "191.168.0.142", 8080);
        HttpProxyClientManager.register("default", "192.168.6.188", 8081);
        HttpProxyClientManager.register("a", "192.168.6.188", 8087);
        HttpProxyClientManager.register("b", "192.168.6.188", 8088);
        HttpProxyClientManager.register("c", "192.168.6.188", 8082);
        super.channelActive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("error", cause);
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object omsg) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("收到请求" + omsg);
        }
        FullHttpRequest msg = (FullHttpRequest) omsg;
        String uri = msg.uri();
        int index = uri.indexOf("/", 1);
        String server = "default";
        if (index > -1 && HttpProxyClientManager.receive(uri.substring(1, index)) != null) {
            server = uri.substring(1, index);
            uri = uri.substring(index);
        }
        if (uri == null || uri.length() == 0) {
            uri = "//";
        }
        msg.setUri(uri);

        HttpProxyClient client = HttpProxyClientManager.receive(server);
        if (client == null) {
            //TODO 返回404
            logger.error("无客户端==============");
            return;
        }
        try {
            HttpHeaderUtil.setKeepAlive(msg, true);
            if (logger.isDebugEnabled()) {
                logger.debug("转发请求" + msg);
            }
            FullHttpResponse response = client.send(msg);
            if (!response.status().equals(HttpResponseStatus.OK)) {
                logger.debug(uri + " state " + response.status());
            }
            if (logger.isDebugEnabled()) {
                logger.debug("转发回复" + response);
            }
            ctx.writeAndFlush(response);
        } catch (Exception e) {
            //TODO 返回404
            logger.error("异常==============", e);
        }

    }

}
