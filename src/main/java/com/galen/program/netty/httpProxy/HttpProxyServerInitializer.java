package com.galen.program.netty.httpProxy;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Created by baogen.zhang on 2018/11/8
 *
 * @author baogen.zhang
 * @date 2018/11/8
 */
public class HttpProxyServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
        p.addLast(new ChunkedWriteHandler());
        //   p.addLast(new HttpContentCompressor());
        //   p.addLast(new HttpContentDecompressor());
        p.addLast(new HttpProxyServerHandler());
    }

}
