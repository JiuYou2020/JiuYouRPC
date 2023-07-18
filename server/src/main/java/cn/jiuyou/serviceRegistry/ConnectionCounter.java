package cn.jiuyou.serviceRegistry;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/25 13:46
 * {@code @Description: }  统计连接数
 */
public class ConnectionCounter {
    private final ConcurrentMap<String, Integer> connectionCounts;

    public ConnectionCounter() {
        this.connectionCounts = new ConcurrentHashMap<>();
    }

    public void incrementConnectionCount(String instanceId) {
        connectionCounts.compute(instanceId, (key, value) -> (value == null) ? 1 : value + 1);
    }

    public void decrementConnectionCount(String instanceId) {
        connectionCounts.computeIfPresent(instanceId, (key, value) -> (value > 0) ? value - 1 : 0);
    }

    public int getConnectionCount(String instanceId) {
        return connectionCounts.getOrDefault(instanceId, 0);
    }
}
