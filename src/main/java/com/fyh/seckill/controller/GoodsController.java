package com.fyh.seckill.controller;

import com.alibaba.druid.util.StringUtils;
import com.fyh.seckill.po.User;
import com.fyh.seckill.redis.GoodsKey;
import com.fyh.seckill.result.Result;
import com.fyh.seckill.service.GoodsService;
import com.fyh.seckill.redis.RedisService;
import com.fyh.seckill.service.UserService;
import com.fyh.seckill.vo.GoodsDetailVo;
import com.fyh.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    /**商品列表
     * 首先从缓存中读取页面，存在则直接返回
     * 如果缓存中没有页面，则通过手动渲染的方式加载页面，并将加载后的页面访日redis缓存中
     *注：
     * 这里渲染的界面是xx.html，是去掉所有动态内容的静态模板，保留在cdn服务器中
     * 实际调用的时候使用的是xx.htm，是动态界面，不过静态资源都是在这个缓存中取出来的
     * */
    @RequestMapping(value = "/to_list", produces = "text/html")//produce指该controller返回的类型为静态页面类型
    @ResponseBody
    public String list(HttpServletRequest request, HttpServletResponse response, Model model, User user){
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsList);

        //手动渲染
        SpringWebContext ctx = new SpringWebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);

        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
        //输出商品列表页面
        return html;
    }

    /**参与秒杀的商品页面
     * 加载策略与列表相同
     * */
    @RequestMapping(value = "/to_detail2/{goodsId}",produces = "text/html")
    @ResponseBody
    public String goodsDetail(HttpServletRequest request, HttpServletResponse response,
                                       Model model, User user, @PathVariable("goodsId") long goodsId){
        model.addAttribute(user);

        String html = redisService.get(GoodsKey.getGoodsDetail, "" + goodsId, String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }

        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goodsVo);

        long startTime = goodsVo.getStartDate().getTime();
        long endTime = goodsVo.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int seckillStates = 0;
        int remainSeconds = 0;

        if(now < startTime){
            seckillStates = 0;//秒杀未开始
            remainSeconds = (int) ((startTime-now)/1000);
        }else if(now > endTime){
            seckillStates = 2;//秒杀已结束
            remainSeconds = -1;
        }else{
            seckillStates = 1;//正在秒杀
            remainSeconds = 0;
        }
        model.addAttribute("seckillStatus", seckillStates);
        model.addAttribute("remainSeconds", remainSeconds);

        //手动渲染
        SpringWebContext ctx = new SpringWebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsDetail, "" + goodsId, html);
        }
        return html;
    }

    /**
     * 商品详情页面
     */
    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response,
                                        Model model, User user, @PathVariable("goodsId") long goodsId) {

        //根据id查询商品详情
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);

        long startTime = goods.getStartDate().getTime();
        long endTime = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int seckillStatus = 0;
        int remainSeconds = 0;

        if (now < startTime) {//秒杀还没开始，倒计时
            seckillStatus = 0;
            remainSeconds = (int) ((startTime - now) / 1000);
        } else if (now > endTime) {//秒杀已经结束
            seckillStatus = 2;
            remainSeconds = -1;
        } else {//秒杀进行中
            seckillStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(user);
        vo.setRemainSeconds(remainSeconds);
        vo.setSeckillStatus(seckillStatus);

        return Result.success(vo);
    }


}
