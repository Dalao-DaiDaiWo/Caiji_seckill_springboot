package com.fyh.seckill.dao;


import com.fyh.seckill.SpringbootSeckillApplication;
import com.fyh.seckill.po.OrderInfo;
import com.fyh.seckill.po.SeckillGoods;
import com.fyh.seckill.po.SeckillOrder;
import com.fyh.seckill.po.User;
import com.fyh.seckill.vo.GoodsVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringbootSeckillApplication.class)
public class DaoTest {
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private UserDao userDao;

    @Test
    public void GoodsDaoTest() {
        List<GoodsVo> goodsVoList = goodsDao.listGoodsVo();
        goodsVoList.forEach((goods) -> {
            System.out.println(goods);
        });
        System.out.println("***************************************************************");
        GoodsVo goodsVo = goodsDao.getGoodsVoByGoodsId(1L);
        System.out.println(goodsVo);
        System.out.println("***************************************************************");
        SeckillGoods sg = new SeckillGoods();
        sg.setGoodsId(goodsVo.getId());
        sg.setVersion(goodsVo.getVersion());
        System.out.println(goodsDao.reduceStockByVersion(sg));
        System.out.println("***************************************************************");
        int version = goodsDao.getVersionByGoodsId(1L);
        System.out.println(version);
    }

    @Test
    public void UserDaoTest() {
        User user = userDao.getById(18181818181L);
        System.out.println(user);
        System.out.println("***************************************************************");
        user.setPassword("b7797cce01b4b131b433b6acf4add423");
        userDao.updatePassword(user);
        System.out.println("***************************************************************");
    }

    @Test
    public void OrderDaoTest() {
        System.out.println(orderDao.getOrderByUserIdGoodsId(18718185897L, 1L));
        System.out.println("***************************************************************");
        User user = userDao.getById(18181818181L);
        GoodsVo goods = goodsDao.getGoodsVoByGoodsId(1L);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getGoodsPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        orderDao.insertOrderInfo(orderInfo);
        System.out.println("***************************************************************");
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(orderInfo.getId());
        seckillOrder.setUserId(user.getId());
        System.out.println(orderDao.insertSeckillOrder(seckillOrder));
        System.out.println("***************************************************************");
        System.out.println(orderDao.getOrderInfoById(10L));
    }

}
