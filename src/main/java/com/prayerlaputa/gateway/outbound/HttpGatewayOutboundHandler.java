package com.prayerlaputa.gateway.outbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author chenglong.yu
 * created on 2020/10/30
 */
public interface HttpGatewayOutboundHandler {
    void handle(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx);
}
