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
 * {@code @Description: } 加权轮询策略
 */
@Slf4j
public class WeightedRoundRobinStrategy implements ProviderStrategy {

    @Override
    public ServiceInstance<Payload> chooseInstance(Collection<ServiceInstance<Payload>> serviceInstances) {
        List<ServiceInstance<Payload>> instanceList = new ArrayList<>(serviceInstances);

        // 计算总权重,权重为instance中payload的weight
        int totalWeight = instanceList.stream().mapToInt(instance -> instance.getPayload().getWeight()).sum();

        // 生成一个随机数，介于[0, totalWeight)之间
        int randomWeight = new Random().nextInt(totalWeight);

        // 累计权重
        int sum = 0;
        for (ServiceInstance<Payload> instance : instanceList) {

            sum += instance.getPayload().getWeight();
            // 如果随机数在某个服务实例的权重范围内，就返回该服务实例
            if (randomWeight < sum) {
                log.info("WeightedRoundRobinStrategy has been used, choose instance: {}", instance);
                return instance;
            }
        }

        // 如果权重都一样，随机返回一个
        log.info("WeightedRoundRobinStrategy has been used, choose instance: {}", instanceList.get(new Random().nextInt(instanceList.size())));
        return instanceList.get(new Random().nextInt(instanceList.size()));
    }
}
