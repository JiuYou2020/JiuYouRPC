package cn.jiuyou.serviceDiscovery.strategies;

import cn.jiuyou.constant.Payload;
import cn.jiuyou.serviceDiscovery.ProviderStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/7/18
 * {@code @Description: } 最小连接数策略
 */
@Slf4j
public class LeastActiveStrategy implements ProviderStrategy {
    @Override
    public ServiceInstance<Payload> chooseInstance(Collection<ServiceInstance<Payload>> serviceInstances) {
        List<ServiceInstance<Payload>> instanceList = new ArrayList<>(serviceInstances);

        // 找出活跃数最小的实例
        ServiceInstance<Payload> leastActiveInstance = instanceList.get(0);
        for (ServiceInstance<Payload> instance : instanceList) {
            if (instance.getPayload().getActiveCount() < leastActiveInstance.getPayload().getActiveCount()) {
                leastActiveInstance = instance;
            }
        }
        log.info("LeastActiveStrategy has been used, choose instance: {}", leastActiveInstance);

        return leastActiveInstance;

    }

}
