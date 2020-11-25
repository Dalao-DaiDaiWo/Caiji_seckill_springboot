package com.fyh.seckill.controller;

import com.fyh.seckill.po.OrderInfo;
import com.fyh.seckill.po.User;
import com.fyh.seckill.result.CodeMsg;
import com.fyh.seckill.result.Result;
import com.fyh.seckill.service.GoodsService;
import com.fyh.seckill.service.OrderService;
import com.fyh.seckill.redis.RedisService;
import com.fyh.seckill.service.UserService;
import com.fyh.seckill.vo.GoodsVo;
import com.fyh.seckill.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model, User user,
                                      @RequestParam("orderId") long orderId){
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo orderInfo = orderService.getOrderById(orderId);
        if(orderInfo == null){
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        Long goodsId = orderInfo.getGoodsId();
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setGoods(goodsVo);
        vo.setOrder(orderInfo);
        return Result.success(vo);
    }
}
