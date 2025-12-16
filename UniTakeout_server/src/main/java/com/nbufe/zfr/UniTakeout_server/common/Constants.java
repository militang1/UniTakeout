package com.nbufe.zfr.UniTakeout_server.common;

public class Constants {
    // JWT密钥
    public static final String JWT_SECRET = "UniTakeoutSecretKey2024";
    // JWT过期时间（7天）
    public static final long JWT_EXPIRATION = 7 * 24 * 60 * 60 * 1000L;
    
    // 验证码有效期（5分钟）
    public static final int CODE_EXPIRATION = 5 * 60;
    // 验证码发送间隔（1分钟）
    public static final int CODE_SEND_INTERVAL = 60;
    
    // 订单状态
    public static final String ORDER_STATUS_PENDING = "pending";
    public static final String ORDER_STATUS_PROCESSING = "processing";
    public static final String ORDER_STATUS_COMPLETED = "completed";
    public static final String ORDER_STATUS_CANCELLED = "cancelled";
    
    // 委托类型
    public static final String DELEGATION_TYPE_REQUEST = "request";
    public static final String DELEGATION_TYPE_OFFER = "offer";
    
    // 委托状态
    public static final String DELEGATION_STATUS_PENDING = "pending";
    public static final String DELEGATION_STATUS_ACCEPTED = "accepted";
    public static final String DELEGATION_STATUS_COMPLETED = "completed";
    
    // 支付方式
    public static final String PAY_METHOD_WECHAT = "wechat";
    public static final String PAY_METHOD_ALIPAY = "alipay";
    public static final String PAY_METHOD_CASH = "cash";
    
    // 配送费规则：满30元免配送费，否则3元
    public static final double FREE_DELIVERY_THRESHOLD = 30.0;
    public static final double DELIVERY_FEE = 3.0;
}

