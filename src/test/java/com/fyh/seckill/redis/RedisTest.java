package com.fyh.seckill.redis;


import com.fyh.seckill.SpringbootSeckillApplication;
import com.fyh.seckill.dao.GoodsDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;



@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringbootSeckillApplication.class)
public class RedisTest {
    @Autowired
    private RedisService redisService;

    @Autowired
    private GoodsDao goodsDao;

    @Test
    public void redisTest(){
        System.out.println("***************************测试GET和SET方法***********************************");
        redisService.set(GoodsKey.getGoodsList, ""+"ceshi", goodsDao.listGoodsVo());
        System.out.println(goodsDao.listGoodsVo());
//        String lists = redisService.get(GoodsKey.getGoodsList, ""+"ceshi", String.class);
//        System.out.println(lists);
//        System.out.println("判断key是否存在:" + redisService.exists(GoodsKey.getGoodsList, ""+"ceshi"));
//        System.out.println("***************************测试DELETE方法***********************************");
//        redisService.delete(GoodsKey.getGoodsList, "");
//        lists = redisService.get(GoodsKey.getGoodsList, "", String.class);
//        System.out.println(lists);
//        System.out.println("判断key是否存在:" + redisService.exists(GoodsKey.getGoodsList, ""));
//        System.out.println("***************************测试增减方法***********************************");
//        redisService.set(GoodsKey.getGoodsList, "ceshi", 1);
//        Integer ceshi = redisService.get(GoodsKey.getGoodsList, "ceshi", Integer.class);
//        System.out.println("初始值：" + ceshi);
//        redisService.incr(GoodsKey.getGoodsList, "ceshi");
//        ceshi = redisService.get(GoodsKey.getGoodsList, "ceshi", Integer.class);
//        System.out.println("增加后：" + ceshi);
//        redisService.decr(GoodsKey.getGoodsList, "ceshi");
//        ceshi = redisService.get(GoodsKey.getGoodsList, "ceshi", Integer.class);
//        System.out.println("减少后：" + ceshi);
    }
}
