package com.fyh.seckill.service;

import com.fyh.seckill.po.OrderInfo;
import com.fyh.seckill.po.SeckillOrder;
import com.fyh.seckill.po.User;
import com.fyh.seckill.redis.RedisService;
import com.fyh.seckill.redis.SeckillKey;
import com.fyh.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeckillService {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisService redisService;

    /**
     * 执行秒杀操作，保证减库存、创建订单（秒杀成功的订单及订单明细） 在同一个事务内
     * */
    @Transactional
    public OrderInfo seckill(User user, GoodsVo goodsVo){
        boolean success = goodsService.reduceStock(goodsVo);
        if(success){
            return orderService.createOrder(user, goodsVo);
        }else{
            setGoodsSellOut(goodsVo.getId());
            return null;
        }
    }

    public long getSeckillResult(long userId, long goodsId){
        SeckillOrder order = orderService.getOrderByUserIdGoodsId(userId, goodsId);
        if(order != null){
            return order.getOrderId();
        }else{
            boolean goodsSellOut = getGoodsSellOut(goodsId);
            if(goodsSellOut){
                return -1;
            }else{
                return 0;
            }
        }
    }


    public void setGoodsSellOut(long goodId){
        redisService.set(SeckillKey.isGoodsOver, ""+goodId, true);
    }

    public boolean getGoodsSellOut(long goodsId){
        return redisService.exists(SeckillKey.isGoodsOver, ""+goodsId);
    }
}
