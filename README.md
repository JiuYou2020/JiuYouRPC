# JiuYouRPC

> 一个简单的RPC框架

jdk版本：1.8

目前可选的序列化方式有：

- JDK原生序列化
- Kryo序列化

目前可选的解压方式有：

- GZIP压缩
- 不压缩
- Deflate压缩

目前可以选择的传输方式有：

- NETTY
- http2

注册中心：

- Zookeeper

负载均衡：

- 随机
- 轮询
- 加权轮询
- 最小连接数

另一种实现方式（使用自带序列化方式）：

- 基于netty+http/2的传输方式
- 超时关闭

遇到问题：

1. 在jdk动态代理时，因为RpcResponse需要封装到http2请求中，因此在传回后JSON反序列化后，其中的data字段的数据类型会丢失，恢复默认的LinkedHashMap，导致报错`java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to cn.jiuyou.entity.User`
,因此在处理时，应先将data转换为User类型再返回
2. 在使用Zookeeper作为服务发现时，在实现负载均衡的最小连接数时，由于在源码中`updateService`需要先判断更新的服务是否在服务列表中，但是判断时不是从Zookeeper中获取的服务列表，而是从本地的`serviceMap`中获取，因此会导致判断错误，因此在`updateService`中加入了判断，如果本地没有该服务，则直接返回，不进行更新，因此在更新service时应该在服务端更新而不是客户端
3. 