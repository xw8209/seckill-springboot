package com.it.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.mapper.GoodsMapper;
import com.it.pojo.Goods;
import com.it.service.IGoodsService;
import com.it.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jobob
 * @since 2022-11-30
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    /**
     * 获取商品列表
     * @return
     */
    @Override
    public List<GoodsVo> findGoodVo() {
        return goodsMapper.findGoodsVo();
    }

    /**
     * 根据id获取商品详情
     *
     * @param goodsId
     * @return
     */
    @Override
    public GoodsVo findGoodVoByGoodsId(Long goodsId) {
        return goodsMapper.findGoodsVoById(goodsId);
    }
}
