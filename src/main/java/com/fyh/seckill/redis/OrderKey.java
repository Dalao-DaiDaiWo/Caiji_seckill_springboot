package com.fyh.seckill.redis;

public class OrderKey extends BasePrefixKey {

    public OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getSeckillOrderByUidGid = new OrderKey("order");

}
