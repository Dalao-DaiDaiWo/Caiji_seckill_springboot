package com.fyh.seckill.controller;

import com.fyh.seckill.po.SeckillOrder;
import com.fyh.seckill.po.User;
import com.fyh.seckill.rabbitmq.MQSender;
import com.fyh.seckill.rabbitmq.SeckillMessage;
import com.fyh.seckill.redis.GoodsKey;
import com.fyh.seckill.result.CodeMsg;
import com.fyh.seckill.result.Result;
import com.fyh.seckill.service.GoodsService;
import com.fyh.seckill.service.OrderService;
import com.fyh.seckill.redis.RedisService;
import com.fyh.seckill.service.SeckillService;
import com.fyh.seckill.vo.GoodsVo;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MQSender sender;
    /**基于令牌桶算法的限流实现类,每秒提供10个令牌*/
    RateLimiter rateLimiter = RateLimiter.create(10);

    /**用来对商品做标记，判断商品是否被处理过*/
    private HashMap<Long, Boolean> localOverMap = new HashMap<>();

    /**
     * 1、使用RateLimiter进行限流
     * 2、使用内存标记判断商品是否售罄
     * 3、使用redis预减库存，判断是否有存货，用户是否可以进行下单
     * 4、使用rabbitMQ实现异步的数据库减库存及订单的创建操作
     * */
    @RequestMapping(value = "/do_seckill", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> list(Model model, User user, @RequestParam("goodsId") long goodsId) throws Exception {
        //令牌桶限流
        if(!rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)){
            return Result.error(CodeMsg.ACCESS_LIMIT_REACHED);
        }

        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        model.addAttribute("user", user);

        //内存标记，通过标志hashmap判断商品是否售罄，减少redis访问
        Boolean over = localOverMap.get(goodsId);
        if(over){
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //redis预减库存
        Long stock = redisService.decr(GoodsKey.getGoodsStock, "" + goodsId);
        if(stock < 0){
            //从数据库中再次读取一次库存，判断是否还有存货
            afterPropertiesSet();
            long stock2 = redisService.decr(GoodsKey.getGoodsStock, "" + goodsId);
            if(stock2 < 0){
                //已售罄
                localOverMap.put(goodsId, true);
                return Result.error(CodeMsg.SECKILL_OVER);
            }
        }
        //判断重复秒杀
        SeckillOrder order = orderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null){
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }
        SeckillMessage message = new SeckillMessage();
        message.setUser(user);
        message.setGoodsId(goodsId);
        //使用消息队列实现异步下单
        sender.sendSeckillMessage(message);
        return Result.success(0);
    }





    /**
     * 判断秒杀的结果：
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> seckillResult(Model model, User user, @RequestParam("goodsId") long goodId){
        model.addAttribute("user", user);
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long orderId = seckillService.getSeckillResult((user.getId()), goodId);
        return Result.success(orderId);
    }




    /**实现spring的InitializingBean接口，具体参考bean的实例化过程，
     * 这里是将数据库里面的商品信息加载到redis和本地内存中*/
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if(goodsVoList == null){
            return;
        }
        for(GoodsVo goods : goodsVoList){
            redisService.set(GoodsKey.getGoodsStock, ""+goods.getId(),goods.getStockCount());
            localOverMap.put(goods.getId(), false);
        }
    }



}
