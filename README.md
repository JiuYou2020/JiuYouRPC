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

待拓展:
- [ ] 服务注册中心
- [ ] 负载均衡