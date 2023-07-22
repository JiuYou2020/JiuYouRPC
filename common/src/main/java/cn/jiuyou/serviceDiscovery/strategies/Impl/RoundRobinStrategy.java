package cn.jiuyou.serviceDiscovery.strategies.Impl;

import cn.jiuyou.constant.Payload;
import cn.jiuyou.serviceDiscovery.strategies.ProviderStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/7/18
 * {@code @Description: } 轮询策略
 */
@Slf4j
public class RoundRobinStrategy implements ProviderStrategy {
    /**
     * 记录当前的服务节点，用于负载均衡的轮询策略
     */
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public ServiceInstance<Payload> chooseInstance(Collection<ServiceInstance<Payload>> serviceInstances) {

        List<ServiceInstance<Payload>> instanceList = new ArrayList<>(serviceInstances);

        // 获取当前索引对应的服务实例
        ServiceInstance<Payload> selectedInstance = instanceList.get(currentIndex.get());

        // 更新索引以选择下一个服务实例
        currentIndex.set((currentIndex.incrementAndGet()) % instanceList.size());
        log.info("RoundRobinStrategy has been used, choose instance: {}", selectedInstance);
        return selectedInstance;
    }
}
