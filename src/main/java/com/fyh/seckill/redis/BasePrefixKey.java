package com.fyh.seckill.redis;

/**为了区别同名的key所描述的对象不同，防止同名重复，因此在key之前加上相应的前缀区分，获得真正存入redis的key
 * 同时根据真正的key来设定相应值的生存时间
 * */
public abstract class BasePrefixKey implements KeyPrefix {

    private int expireSecond;

    private String prefix;

    public BasePrefixKey(String prefix){
        this(0,prefix);
    }

    public BasePrefixKey(int expireSecond, String prefix){
        this.expireSecond = expireSecond;
        this.prefix = prefix;
    }

    public int getExpireSeconds(){
        return expireSecond;
    }

    public String getPrefix(){
        return prefix;
    }

}
