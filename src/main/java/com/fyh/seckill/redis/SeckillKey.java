package com.fyh.seckill.redis;

public class SeckillKey extends BasePrefixKey {

    public SeckillKey(String prefix) {
        super(prefix);
    }

    public static SeckillKey isGoodsOver = new SeckillKey("go");
}
