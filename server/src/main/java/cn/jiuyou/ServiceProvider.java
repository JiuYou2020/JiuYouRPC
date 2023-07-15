package cn.jiuyou;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/25 01:46
 * {@code @Description: } 用来提供Service实例
 */
public class ServiceProvider {
    private final Map<String, Object> map;

    public ServiceProvider() {
        map = new ConcurrentHashMap<>();
    }

    public void addService(Object service) {
        Class<?>[] interfaces = service.getClass().getInterfaces();
        for (Class<?> anInterface : interfaces) {
            map.put(anInterface.getName(), service);
        }
    }

    public Object getService(String interfaceName) {
        return map.get(interfaceName);
    }
}
