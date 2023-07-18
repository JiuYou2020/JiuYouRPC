# JiuYouRPC
> 一个简单的RPC框架

目前可选的序列化方式有：
- JDK原生序列化
- Kryo序列化

目前可选的解压方式有：

- GZIP压缩
- 不压缩
- Deflate压缩

目前可以选择的传输方式有：

- BIO
- NETTY
- BIO+线程池

注册中心：

- Zookeeper

负载均衡：

- 随机
- 轮询
- 加权轮询
- 最小连接数

待拓展:

- [ ] 心跳机制
- [ ] 基于http/2的传输方式