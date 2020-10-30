package com.prayerlaputa.gateway.outbound.okhttp;

import com.prayerlaputa.gateway.outbound.HttpGatewayOutboundHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author chenglong.yu
 */
public class OkhttpOutboundHandler implements HttpGatewayOutboundHandler {
    @Override
    public void handle(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {

    }
}
