package com.fyh.seckill.service;

import com.fyh.seckill.dao.OrderDao;
import com.fyh.seckill.po.OrderInfo;
import com.fyh.seckill.po.SeckillOrder;
import com.fyh.seckill.po.User;
import com.fyh.seckill.redis.OrderKey;
import com.fyh.seckill.redis.RedisService;
import com.fyh.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;


@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private RedisService redisService;

    /**
     * 获取秒杀成功的订单
     * */
    public SeckillOrder getOrderByUserIdGoodsId(long userId, long goodsId){
        return redisService.get(OrderKey.getSeckillOrderByUidGid, ""+userId+"_"+goodsId, SeckillOrder.class);
    }

    /**
     * 获取秒杀成功的订单明细
     * */
    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderInfoById(orderId);
    }

    /**
     * 分别在订单表和订单明细表增加一条秒杀成功的订单数据，要保证两个订单的创建在同一个事务里
     * */
    @Transactional
    public OrderInfo createOrder(User user, GoodsVo goodsVo){
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setGoodsPrice(goodsVo.getGoodsPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        orderDao.insertOrderInfo(orderInfo);

        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId(user.getId());
        orderDao.insertSeckillOrder(seckillOrder);

        redisService.set(OrderKey.getSeckillOrderByUidGid,""+user.getId()+"_"+goodsVo.getId(), seckillOrder);

        return orderInfo;
    }
}
