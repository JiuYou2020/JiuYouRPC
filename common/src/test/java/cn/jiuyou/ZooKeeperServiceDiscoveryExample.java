package cn.jiuyou;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;

public class ZooKeeperServiceDiscoveryExample {

    public static void main(String[] args) throws Exception {
        String connectionString = "zookeeper1:2181,zookeeper2:2181,zookeeper3:2181"; // ZooKeeper集群的连接字符串
        int sessionTimeoutMs = 5000; // 会话超时时间
        int connectionTimeoutMs = 5000; // 连接超时时间
        String serviceName = "my-service"; // 服务名称

        // 使用指数退避重试策略创建Curator客户端
        CuratorFramework client = CuratorFrameworkFactory.newClient(connectionString, sessionTimeoutMs, connectionTimeoutMs, new ExponentialBackoffRetry(1000, 3));
        client.start(); // 启动客户端

        // 创建 ServiceDiscovery 实例
        ServiceDiscovery<String> serviceDiscovery = ServiceDiscoveryBuilder.builder(String.class)
                .client(client)
                .basePath("/services") // 服务注册的基路径
                .serializer(new JsonInstanceSerializer<>(String.class)) // 使用 JSON 序列化
                .build();

        serviceDiscovery.start(); // 启动 ServiceDiscovery

        // 注册服务实例
        ServiceInstance<String> serviceInstance = ServiceInstance.<String>builder()
                .name(serviceName)
                .address("localhost") // 服务实例的地址
                .port(8080) // 服务实例的端口
                .build();

        serviceDiscovery.registerService(serviceInstance); // 注册服务实例

        // 发现服务实例
        Collection<ServiceInstance<String>> instances = serviceDiscovery.queryForInstances(serviceName);
        for (ServiceInstance<String> instance : instances) {
            String address = instance.getAddress();
            int port = instance.getPort();
            System.out.println("Found service instance: " + address + ":" + port);
            // 处理服务实例
            // ...
        }

        // 关闭服务发现和客户端
        serviceDiscovery.close();
        client.close();
    }
}
