package com.prayerlaputa.gateway.backend.netty;

/**
 * @author chenglong.yu
 * created on 2020/10/31
 */
public class NettyBackendServerDemo {
    public static void main(String[] args) {
        HttpServer server = new HttpServer(false,18807);
        try {
            server.run();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
