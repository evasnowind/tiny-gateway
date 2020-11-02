# tiny-gateway
An API gateway based on Netty 4.


## 使用说明  

### 程序启动  
1. 启动test/java/com/prayerlaputa/gateway/backend/netty/NettyBackendServerDemo，作为后端应用，监听本地18805、18806、18807端口。
2. 启动src/main/java/com/prayerlaputa/gateway/NettyServerApplication，作为网关程序，监听本地18808端口，会将请求转发给本地18805、18806、18807端口.    
3. 使用postman/浏览器，访问 localhost:18008/test，将返回字符串"hello, gateway"，则测试成功。

目前是通过代码写死的方式，默认采用Round Robin路由策略，这块可以后续写活，时间不充裕就先这样吧。

注意目前各种代码写死，先写个初版。只写了OkHttpOutboundHandler能支持filter、router，暂时还没将filter、router抽象成比较公共、方便替换的模块。




## 更新记录

| 更新时间   | 版本 | 内容                                                         |
| ---------- | ---- | ------------------------------------------------------------ |
| 2020-10-30 | 0.1  | 提交初步版本，<br/>可以使用httpclient/okhttp在Netty中<br/>进行请求转发 |
| 2020-11-02 | 0.2  | 采用模板方法模式，定义处理请求的前后可以调用filter；<br/>OkHttpOutboundHandler添加一个filter，以便在request中<br/>添加一个nio头。 |
| 2020-11-02 | 0.3  | 添加了hash和RR的router                                       |



## 实现思路

### filter的实现

- 采用模板方法模式，定义了一个基类HttpGatewayOutboundWithHookHandler，所有OutboundHandler都需要继承该类并实现其processRequest方法。
  - processRequest用于实现请求转发逻辑。
  - 提供了addHookBeforeHandlingRequest、addHookAfterHandledRequest接口来为processRequest方法添加filter，filter将在processRequest执行之前、执行之后执行业务逻辑。
  - 外部调用OutboundHandler时，需要调用HttpGatewayOutboundWithHookHandler#handle方法。
  - filter必须实现HttpRequestFilter接口，目前已实现的HttpHeaderRequestFilter，功能是在网关将请求转发前，为请求添加一个header，内容为("nio", "hello")，该header会在转发时同步传给后端应用。

### router的实现

- 服务器地址列表保存在OkHttpOutboundHandler中，每次处理请求时，根据不同的路由策略从地址列表中拿出相应的地址，然后继续走后续的逻辑。
- hash策略：HashHttpEndpointRouter
  - 生成一个列表范围内的随机数，比较容易
- RR策略：RoundRobinEndpointRouter
  - 保存一个map，map中保存 **<列表hash值，下一个需要将要访问的地址>**。每调用一次，都将map中的索引位置+1
  - 缺点：RoundRobinEndpointRouter一直维护着这个映射关系。

### 后续扩展思路

限于时间，没有实现的代码：

- 后端服务启动时，实现一个简单的服务注册功能，直接将启动的服务告知网关程序，网关接收注册请求，将服务地址信息保存起来。
- 使用配置文件来指定filter, router



## 参考资料
- [kimmking JavaCourse](https://github.com/kimmking/JavaCourseCodes) 
