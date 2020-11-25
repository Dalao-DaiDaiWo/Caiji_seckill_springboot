package com.fyh.seckill.vo;

import com.fyh.seckill.po.User;

public class GoodsDetailVo {

    private Integer seckillStatus = 0;
    private Integer remainSeconds = 0;
    private GoodsVo goods ;
    private User user;

    public int getSeckillStatus(int seckillStates) {
        return seckillStatus;
    }

    public void setSeckillStatus(int seckillStatus) {
        this.seckillStatus = seckillStatus;
    }

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

    public GoodsVo getGoods() {
        return goods;
    }

    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
