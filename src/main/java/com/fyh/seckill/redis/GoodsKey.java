package com.fyh.seckill.redis;

public class GoodsKey extends BasePrefixKey {

    private GoodsKey(int expireSecond, String prefix) {
        super(expireSecond, prefix);
    }

    public static GoodsKey getGoodsList = new GoodsKey(60, "gl");
    public static GoodsKey getGoodsDetail = new GoodsKey(60, "gd");
    public static GoodsKey getGoodsStock = new GoodsKey(0, "gs");
}
