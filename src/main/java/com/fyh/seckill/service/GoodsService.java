package com.fyh.seckill.service;

import com.fyh.seckill.dao.GoodsDao;
import com.fyh.seckill.po.SeckillGoods;
import com.fyh.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class GoodsService {

    private static final int DEFAULT_MAX_RETRIES = 5;

    @Autowired
    private GoodsDao goodsDao;

    /**
     * 查看所有货物
     * */
    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }

    /**
     * 根据货物名称获取货物信息
     * */
    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    /**
     * 执行秒杀动作，秒杀是根据库存判断及添加版本号Version实现乐观锁的方式实现的，
     * 如果goodsDao.reduceStockByVersion（）失败会抛出异常，并继续尝试秒杀，直到达到最大尝试次数DEFAULT_MAX_RETRIES
     **/
    public boolean reduceStock(GoodsVo goods){
        int numAttempts = 0;
        int ret = 0;
        SeckillGoods sg = new SeckillGoods();
        sg.setGoodsId(goods.getId());
        sg.setVersion(goods.getVersion());
        do{
            numAttempts++;
            try{
                sg.setVersion(goodsDao.getVersionByGoodsId(goods.getId()));
                ret = goodsDao.reduceStockByVersion(sg);
            }catch (Exception e){
                e.printStackTrace();
            }
            if(ret != 0)
                break;
        }while(numAttempts < DEFAULT_MAX_RETRIES);
        return ret > 0;
    }


}
