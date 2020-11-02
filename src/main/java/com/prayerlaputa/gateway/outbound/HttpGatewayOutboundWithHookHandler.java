package com.prayerlaputa.gateway.outbound;

import com.prayerlaputa.gateway.filter.HttpRequestFilter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 定义一个带有hook的handler基类。
 * 使用模板方法模式，将处理请求前后调用hook的逻辑封装起来。
 * 子类只需要实现processRequest方法，处理请求即可。
 *
 * @author chenglong.yu
 * created on 2020/11/2
 */
public class HttpGatewayOutboundWithHookHandler{

    /**
     * 此处可以进一步扩展：添加filter时使用TreeMap,保存
     */
    List<HttpRequestFilter> hooksBeforeHandlingRequest = new ArrayList<>();
    List<HttpRequestFilter> hooksAfterHandledRequest = new ArrayList<>();

    public void handle(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx) throws NoSuchMethodException {
        if (!hooksBeforeHandlingRequest.isEmpty()) {
            //执行处理请求前的filter
            for (HttpRequestFilter filter : hooksBeforeHandlingRequest) {
                filter.filter(fullRequest, ctx);
            }
        }

        processRequest(fullRequest, ctx);

        if (!hooksAfterHandledRequest.isEmpty()) {
            //执行处理请求后的filter
            for (HttpRequestFilter filter : hooksAfterHandledRequest) {
                filter.filter(fullRequest, ctx);
            }
        }
    }

    public void processRequest(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx) throws NoSuchMethodException{
        throw new NoSuchMethodException("processRequest方法没有被定义！");
    }

    public void addHookBeforeHandlingRequest(HttpRequestFilter filter) {
        hooksBeforeHandlingRequest.add(filter);
    }

    public void addHookAfterHandledRequest(HttpRequestFilter filter) {
        hooksAfterHandledRequest.add(filter);
    }

}
