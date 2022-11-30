package com.it.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.pojo.Goods;
import com.it.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2022-11-30
 */
public interface IGoodsService extends IService<Goods> {
    /**
     * 获取商品列表页面
     * @return
     */
    List<GoodsVo> findGoodVo();
}
