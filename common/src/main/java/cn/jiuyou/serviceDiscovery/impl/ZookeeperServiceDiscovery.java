package cn.jiuyou.serviceDiscovery.impl;

import cn.jiuyou.constant.Payload;
import cn.jiuyou.serviceDiscovery.Discovery;
import cn.jiuyou.serviceDiscovery.strategies.ProviderStrategy;
import cn.jiuyou.serviceDiscovery.strategies.Impl.RandomStrategy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;

import static cn.jiuyou.constant.Constants.*;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/7/18
 * {@code @Description: } ZookeeperServiceDiscovery 服务发现
 */
public class ZookeeperServiceDiscovery implements Discovery {
    private final CuratorFramework client;
    private final ServiceDiscovery<Payload> serviceDiscovery;
    private volatile static ZookeeperServiceDiscovery INSTANCE;
    private static ProviderStrategy providerStrategy;

    private ZookeeperServiceDiscovery() throws Exception {
        client = CuratorFrameworkFactory.newClient(CONNECTION_STRING, SESSION_TIMEOUT_MS, CONNECTION_TIMEOUT_MS, new ExponentialBackoffRetry(1000, 3));
        client.start();
        //创建 ServiceDiscovery实例
        serviceDiscovery = ServiceDiscoveryBuilder.builder(Payload.class)
                .client(client)
                .basePath(BASE_PATH)
                .serializer(new JsonInstanceSerializer<>(Payload.class))
                .build();

        serviceDiscovery.start();
    }

    @Override
    public void registerService(ServiceInstance<Payload> serviceInstance) throws Exception {
        serviceDiscovery.registerService(serviceInstance);
    }

    @Override
    public void unregisterService(ServiceInstance<Payload> serviceInstance) throws Exception {
        serviceDiscovery.unregisterService(serviceInstance);
    }

    @Override
    public Collection<ServiceInstance<Payload>> queryForInstances(String serviceName) throws Exception {
        Collection<ServiceInstance<Payload>> serviceInstances = serviceDiscovery.queryForInstances(serviceName);
        if (serviceInstances.isEmpty()) {
            throw new RuntimeException("No instances available for service: " + serviceName);
        }
        return serviceInstances;

    }

    @Override
    public ServiceInstance<Payload> getInstanceByStrategy(String serviceName) throws Exception {
        Collection<ServiceInstance<Payload>> serviceInstances = queryForInstances(serviceName);
        return providerStrategy.chooseInstance(serviceInstances);
    }

    @Override
    public void updateService(ServiceInstance<Payload> serviceInstance) throws Exception {
        serviceDiscovery.updateService(serviceInstance);
    }

    public static ZookeeperServiceDiscovery getInstance() throws Exception {
        if (INSTANCE == null) {
            synchronized (ZookeeperServiceDiscovery.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ZookeeperServiceDiscovery();
                    if (providerStrategy == null) {
                        providerStrategy = new RandomStrategy();
                    }
                }
            }
        }
        return INSTANCE;
    }


    public static ProviderStrategy getProviderStrategy() {
        return providerStrategy;
    }

    public static void setProviderStrategy(ProviderStrategy providerStrategy) {
        ZookeeperServiceDiscovery.providerStrategy = providerStrategy;
    }


    public void close() throws Exception {
        serviceDiscovery.close();
        client.close();
    }
}
