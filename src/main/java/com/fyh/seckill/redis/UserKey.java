package com.fyh.seckill.redis;

public class UserKey extends BasePrefixKey {

    public static final int TOKEN_EXPIRE = 3600*24 *2;//默认两天

    public UserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static UserKey token = new UserKey(TOKEN_EXPIRE,"token");
    public static UserKey getById = new UserKey(0, "id");
}
