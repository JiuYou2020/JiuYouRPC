package cn.jiuyou.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/24 22:05
 * {@code @Description: } RPC请求体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcRequest implements Serializable {
    /**
     * 接口名
     */
    private String interfaceName;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 方法参数
     */
    private Object[] params;
    /**
     * 方法参数的类型
     */
    private Class<?>[] paramTypes;
}
