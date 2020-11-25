package com.fyh.seckill.redis;

/**模板方法设计模式*/
public interface KeyPrefix {

    int getExpireSeconds();

    String getPrefix();
}
