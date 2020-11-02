package com.prayerlaputa.gateway.inbound;

import com.prayerlaputa.gateway.outbound.HttpGatewayOutboundWithHookHandler;
import com.prayerlaputa.gateway.outbound.okhttp.OkHttpOutboundHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpInboundHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(HttpInboundHandler.class);
    private final String proxyServer;
    private HttpGatewayOutboundWithHookHandler handler;
    
    public HttpInboundHandler(String proxyServer) {
        this.proxyServer = proxyServer;
//        handler = new HttpOutboundHandler(this.proxyServer);
        handler = new OkHttpOutboundHandler(this.proxyServer);

        /*
        TODO 这里是创建channel时调用的，目前的实现中，每次创建一个新的channel，都会调用，
         后面可以这样：使用Netty中的单例模式（@Shareable），优化此处的handler，维持一个单独的线程池处理网络请求，共享handler。
         */
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            //logger.info("channelRead流量接口请求开始，时间为{}", startTime);
            FullHttpRequest fullRequest = (FullHttpRequest) msg;
            handler.handle(fullRequest, ctx);
        } catch(Exception e) {
            logger.error("HttpInboundHandler#channelRead: ", e);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
