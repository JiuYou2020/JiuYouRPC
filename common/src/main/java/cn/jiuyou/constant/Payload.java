package cn.jiuyou.constant;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/7/18
 * {@code @Description: } zk节点的payload需要有什么，例如这里利用这个实现权重
 */
public class Payload {
    private final AtomicInteger weight;
    private final AtomicInteger activeCount = new AtomicInteger(0);

    public Payload(@JsonProperty("weight") int weight) {
        this.weight = new AtomicInteger(weight);
    }

    public int getWeight() {
        return weight.get();
    }

    public int getActiveCount() {
        return activeCount.get();
    }

    public void incrementActiveCount() {
        activeCount.incrementAndGet();
    }

    public void decrementActiveCount() {
        activeCount.decrementAndGet();
    }
}
