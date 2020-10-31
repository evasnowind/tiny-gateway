package com.prayerlaputa.gateway.outbound.okhttp;

import com.prayerlaputa.gateway.inbound.HttpInboundServer;
import com.prayerlaputa.gateway.outbound.HttpGatewayOutboundHandler;
import com.prayerlaputa.gateway.util.NamedThreadFactory;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.Header;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Time;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author chenglong.yu
 */
public class OkHttpOutboundHandler implements HttpGatewayOutboundHandler {

    private static Logger logger = LoggerFactory.getLogger(OkHttpOutboundHandler.class);

    private OkHttpClient client;
    private String backendUrl;

    public OkHttpOutboundHandler(String backendUrl) {
        this.backendUrl = backendUrl.endsWith("/") ? backendUrl.substring(0, backendUrl.length() - 1) : backendUrl;

        client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                //TODO okhttp可以手工指定连接池，这里可以稍微瞅瞅怎么设置比较优雅，是否需要额外指定线程池来执行
                .build();
    }

    @Override
    public void handle(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
        final String url = this.backendUrl + fullRequest.uri();
        processGetRequest(fullRequest, ctx, url);
    }

    private void processGetRequest(final FullHttpRequest fullHttpRequest, final ChannelHandlerContext ctx, final String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                logger.error("processGetRequest failure:", e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                forwardResponse(fullHttpRequest, ctx, response);
            }
        });
    }

    private void forwardResponse(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, Response response) {
        FullHttpResponse resp = null;
        try {
            byte[] respBody = response.body().bytes();

//            System.out.println("收到相应数据：" + new String(respBody));

            resp = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(respBody));
            resp.headers().set("Content-Type", "application/json");

            Headers responseHeaders = response.headers();
            for (int i = 0; i < responseHeaders.size(); i++) {
                resp.headers().set(responseHeaders.name(i), responseHeaders.value(i));
            }
        } catch (Exception e) {
            logger.error("forwardResponse error:", e);
            resp = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
        } finally {
            if (null != fullRequest) {
                if (!HttpUtil.isKeepAlive(fullRequest)) {
                    ctx.write(resp).addListener(ChannelFutureListener.CLOSE);
                } else {
                    //response.headers().set(CONNECTION, KEEP_ALIVE);
                    ctx.write(resp);
                }
            }
            ctx.flush();
        }

    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("error:", cause);
        ctx.close();
    }
}
