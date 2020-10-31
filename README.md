# tiny-gateway
An API gateway based on Netty 4.


## 使用说明  

为方便测试，在test/java/com/prayerlaputa/gateway/backend中给出了个简单的模拟后端应用的程序。  
测试时，先启动test/java/com/prayerlaputa/gateway/backend/netty/NettyBackendServerDemo，作为后端应用，监听本地18807端口。   
然后启动src/main/java/com/prayerlaputa/gateway/NettyServerApplication，作为网关程序，监听本地18808端口，会将请求转发给18807.  


## 更新记录

| 更新时间   | 版本 | 内容                                                         |
| ---------- | ---- | ------------------------------------------------------------ |
| 2020-10-30 | 0.1  | 提交初步版本，<br/>可以使用httpclient/okhttp在Netty中<br/>进行请求转发 |
|            |      |                                                              |
|            |      |                                                              |



## 参考资料
- [kimmking JavaCourse](https://github.com/kimmking/JavaCourseCodes) 
