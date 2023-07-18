package cn.jiuyou.serviceDiscovery.strategies;

import cn.jiuyou.constant.Payload;
import cn.jiuyou.serviceDiscovery.ProviderStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/7/18
 * {@code @Description: } 随机策略
 */
@Slf4j
public class RandomStrategy implements ProviderStrategy {
    @Override
    public ServiceInstance<Payload> chooseInstance(Collection<ServiceInstance<Payload>> serviceInstances) {
        List<ServiceInstance<Payload>> instanceList = new ArrayList<>(serviceInstances);
        int randomIndex = new Random().nextInt(instanceList.size());
        log.info("RandomStrategy has been used, choose instance: {}", instanceList.get(randomIndex));
        return instanceList.get(randomIndex);
    }
}
