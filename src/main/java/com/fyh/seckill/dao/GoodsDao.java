package com.fyh.seckill.dao;

import com.fyh.seckill.po.SeckillGoods;
import com.fyh.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


import java.util.List;


@Repository
public interface GoodsDao {

    List<GoodsVo> listGoodsVo();

    GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

    int reduceStockByVersion(SeckillGoods seckillGoods);

    int getVersionByGoodsId(@Param("goodsId") long goodsId);

}
