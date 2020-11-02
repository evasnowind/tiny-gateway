# tiny-gateway
An API gateway based on Netty 4.


## 使用说明  

### 程序启动  
1. 启动test/java/com/prayerlaputa/gateway/backend/netty/NettyBackendServerDemo，作为后端应用，监听本地18807端口。
2. 启动src/main/java/com/prayerlaputa/gateway/NettyServerApplication，作为网关程序，监听本地18808端口，会将请求转发给18807.    
3. 使用postman/浏览器，访问 localhost:18008/test，将返回字符串"hello, gateway"，则测试成功。




## 更新记录

| 更新时间   | 版本 | 内容                                                         |
| ---------- | ---- | ------------------------------------------------------------ |
| 2020-10-30 | 0.1  | 提交初步版本，<br/>可以使用httpclient/okhttp在Netty中<br/>进行请求转发 |
| 2020-11-02 | 0.2  | 采用模板方法模式，定义处理请求的前后可以调用filter；<br/>OkHttpOutboundHandler添加一个filter，以便在request中<br/>添加一个nio头。 |
|            |      |                                                              |



## 实现思路

### filter的实现

- 采用模板方法模式，定义了一个基类HttpGatewayOutboundWithHookHandler，所有OutboundHandler都需要继承该类并实现其processRequest方法。
  - processRequest用于实现请求转发逻辑。
  - 提供了addHookBeforeHandlingRequest、addHookAfterHandledRequest接口来为processRequest方法添加filter，filter将在processRequest执行之前、执行之后执行业务逻辑。
  - 外部调用OutboundHandler时，需要调用HttpGatewayOutboundWithHookHandler#handle方法。
  - filter必须实现HttpRequestFilter接口，目前已实现的HttpHeaderRequestFilter，功能是在网关将请求转发前，为请求添加一个header，内容为("nio", "hello")，该header会在转发时同步传给后端应用。



### 后续扩展思路

限于时间，没有实现的代码：

- 后端服务启动时，实现一个简单的服务注册功能，直接将启动的服务告知网关程序，网关接收注册请求，将服务地址信息保存起来。



## 参考资料
- [kimmking JavaCourse](https://github.com/kimmking/JavaCourseCodes) 
