package com.fyh.seckill.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 根据redis参数生产连接池，并将连接池jedisPool注入Bean
 * */
@Service
public class RedisPoolFactory {

    @Autowired
    private RedisParam redisParam;

    @Bean
    public JedisPool JedisPoolFactory(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(redisParam.getPoolMaxIdle());
        config.setMaxTotal(redisParam.getPoolMaxTotal());
        config.setMaxWaitMillis(redisParam.getPoolMaxWait() * 1000);
        JedisPool jedisPool = new JedisPool(config, redisParam.getHost(), redisParam.getPort(),
                redisParam.getTimeout() * 1000, redisParam.getPassword(), 0);
        return jedisPool;
    }
}
