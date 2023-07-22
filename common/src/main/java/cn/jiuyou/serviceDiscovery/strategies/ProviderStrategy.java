package cn.jiuyou.serviceDiscovery.strategies;

import cn.jiuyou.constant.Payload;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.Collection;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/7/18
 * {@code @Description: } 服务提供者的负载均衡策略
 */
public interface ProviderStrategy {
    /**
     * 选择一个服务实例
     *
     * @param serviceInstances 服务实例集合
     * @return 服务实例
     */
    ServiceInstance<Payload> chooseInstance(Collection<ServiceInstance<Payload>> serviceInstances);
}
