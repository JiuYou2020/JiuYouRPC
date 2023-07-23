package cn.jiuyou;

import cn.jiuyou.constant.Payload;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.jiuyou.constant.Constants.HOST;
import static cn.jiuyou.constant.Constants.PORT;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/25 01:46
 * {@code @Description: } 用来提供Service实例,并创建Service实例，在运行服务端时，会将服务端的所有服务都注册到Zookeeper上
 */
public class ServiceProvider {
    private static final Map<String, Object> MAP = new ConcurrentHashMap<>();
    public static final Map<String, ServiceInstance<Payload>> SERVICE_MAP = new ConcurrentHashMap<>();

    public void addService(Object service) throws Exception {
        Class<?>[] interfaces = service.getClass().getInterfaces();
        for (Class<?> anInterface : interfaces) {
            // 在这里构建名称，如果是同一个接口,名称就会重复,将同一个接口的后面加上不同的数字，这样同一个服务就可以注册多次 例如：cn.jiuyou.HelloService1
            String name = anInterface.getName();
            int weight = 1;
            while (MAP.containsKey(name)) {
                name = name + weight++;
            }
            MAP.put(anInterface.getName(), service);
            // 构建 ServiceInstance 对象，但不注册服务
            ServiceInstance<Payload> serviceInstance = ServiceInstance.<Payload>builder()
                    .name(anInterface.getName())
                    .address(HOST)
                    .port(PORT)
                    .payload(new Payload(1))
                    .build();
            SERVICE_MAP.put(name, serviceInstance);
        }
    }

    public void addService(Object service, int weight) throws Exception {
        Class<?>[] interfaces = service.getClass().getInterfaces();
        for (Class<?> anInterface : interfaces) {
            // 在这里构建名称，如果是同一个接口,名称就会重复,将同一个接口的后面加上不同的数字，这样同一个服务就可以注册多次 例如：cn.jiuyou.HelloService1
            String name = anInterface.getName();
            int temp = 1;
            while (MAP.containsKey(name)) {
                name = name + temp++;
            }
            MAP.put(anInterface.getName(), service);
            // 构建 ServiceInstance 对象，但不注册服务
            ServiceInstance<Payload> serviceInstance = ServiceInstance.<Payload>builder()
                    .name(anInterface.getName())
                    .address(HOST)
                    .port(PORT)
                    .payload(new Payload(weight))
                    .build();
            SERVICE_MAP.put(name, serviceInstance);
        }
    }

    /**
     * 获取需要注册的所有服务实例,从ServiceMap中
     *
     * @return ArrayList<ServiceInstance < Payload>>
     */

    public static ArrayList<ServiceInstance<Payload>> getServiceInstances() {
        return new ArrayList<>(SERVICE_MAP.values());
    }

    public static ServiceInstance<Payload> getServiceInstance(String interfaceName) {
        return SERVICE_MAP.get(interfaceName);
    }


    public static Object getService(String interfaceName) {
        return MAP.get(interfaceName);
    }
}
