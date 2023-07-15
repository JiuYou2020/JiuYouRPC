package cn.jiuyou.constant;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/25 22:58
 * {@code @Description: } 消息类型枚举类
 */
public enum MessageType {
    /**
     * RPC请求
     */
    RPC_REQUEST(1),
    /**
     * RPC响应
     */
    RPC_RESPONSE(2),
    /**
     * PING消息
     */
    PING(3),
    /**
     * PONG消息
     */
    PONG(4);
    /**
     * 消息类型码
     */
    private final int code;

    MessageType(int code) {
        this.code = code;
    }

    public static MessageType getEnumByTypeCode(int code) {
        for (MessageType messageType : MessageType.values()) {
            if (messageType.getCode() == code) {
                return messageType;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }
}
