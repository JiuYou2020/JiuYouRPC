package cn.jiuyou.serviceDiscovery;

import cn.jiuyou.constant.Payload;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.Collection;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/7/18
 * {@code @Description: }服务注册与发现，应该有注册，取消注册和服务发现的功能
 */
public interface Discovery {
    /**
     * 注册服务
     *
     * @param serviceInstance 服务实例
     * @throws Exception 注册失败抛出异常
     */
    void registerService(ServiceInstance<Payload> serviceInstance) throws Exception;

    /**
     * 取消注册服务
     *
     * @param serviceInstance 服务实例
     * @throws Exception 取消注册失败抛出异常
     */

    void unregisterService(ServiceInstance<Payload> serviceInstance) throws Exception;

    /**
     * 查询服务
     *
     * @param serviceName 服务名称
     * @return 服务实例集合
     * @throws Exception 查询失败抛出异常
     */
    Collection<ServiceInstance<Payload>> queryForInstances(String serviceName) throws Exception;

    /**
     * 根据负载均衡策略获取服务实例
     *
     * @param serviceName 服务名称
     * @return 服务实例
     * @throws Exception 获取失败抛出异常
     */
    ServiceInstance<Payload> getInstanceByStrategy(String serviceName) throws Exception;

    /**
     * 更新服务到zk
     *
     * @param serviceInstance serviceInstance
     * @throws Exception Exception
     */
    void updateService(ServiceInstance<Payload> serviceInstance) throws Exception;
}
