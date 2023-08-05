# JiuYouRPC

> 一个简单的RPC框架，实现了服务注册与发现，负载均衡，序列化，动态代理等功能
> - 服务注册与发现：使用Zookeeper作为注册中心，客户端通过Zookeeper获取服务列表，服务端将服务注册到Zookeeper中
> - 负载均衡：客户端通过负载均衡算法从服务列表中选择一个服务进行调用
> - 序列化：默认使用Kryo序列化，提高序列化效率
> - 使用ssl加密传输
> - 动态代理：服务端/客户端通过动态代理调用服务端的方法
> - 使用Promise模式处理异步请求，使用snowflake算法生成全局唯一id保证幂等性

jdk版本：1.8

目前可选的序列化方式有：

- JDK原生序列化
- Kryo序列化

注册中心：

- Zookeeper

负载均衡：

- 随机
- 轮询
- 加权轮询
- 最小连接数


遇到问题：

1. 在jdk动态代理时，因为RpcResponse需要封装到http2请求中，因此在传回后JSON反序列化后，其中的data字段的数据类型会丢失，恢复默认的LinkedHashMap，导致报错`java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to cn.jiuyou.entity.User`
,因此在处理时，应先将data转换为User类型再返回
2. 在使用Zookeeper作为服务发现时，在实现负载均衡的最小连接数时，由于在源码中`updateService`需要先判断更新的服务是否在服务列表中，但是判断时不是从Zookeeper中获取的服务列表，而是从本地的`serviceMap`中获取，因此会导致判断错误，因此在`updateService`中加入了判断，如果本地没有该服务，则直接返回，不进行更新，因此在更新service时应该在服务端更新而不是客户端