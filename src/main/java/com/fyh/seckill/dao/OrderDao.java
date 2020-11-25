package com.fyh.seckill.dao;

import com.fyh.seckill.po.OrderInfo;
import com.fyh.seckill.po.SeckillOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderDao {

    SeckillOrder getOrderByUserIdGoodsId(@Param("userId") long userId, @Param("goodsId") long goodsId);

    long insertOrderInfo(OrderInfo orderInfo);

    int insertSeckillOrder(SeckillOrder order);

    OrderInfo getOrderInfoById(@Param("orderId")long orderId);
}
