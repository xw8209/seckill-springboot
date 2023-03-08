package com.it.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.mapper.OrderMapper;
import com.it.pojo.Order;
import com.it.pojo.SeckillGoods;
import com.it.pojo.SeckillOrder;
import com.it.pojo.User;
import com.it.service.IGoodsService;
import com.it.service.IOrderService;
import com.it.service.ISeckillGoodsService;
import com.it.service.ISeckillOrderService;
import com.it.vo.GoodsVo;
import com.it.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jobob
 * @since 2022-11-30
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IGoodsService goodsService;
    @Transactional
    @Override
    public Order seckill(User user, GoodsVo goods) {
        //秒杀商品减库存
        SeckillGoods seckillgoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goods.getId()));
        //seckillgoods.setStockCount(seckillgoods.getStockCount() - 1);
        boolean seckillGoodsResult = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count = stock_count - 1").eq("goods_id",seckillgoods.getId()).gt("stock_count",0));
        if(seckillgoods.getStockCount() < 1){
            //判断是否还有库存
            redisTemplate.opsForValue().set("isStockEmpty" + goods.getId(),"0");
            return null;
        }
        //        if(!seckillGoodsResult){
//            return null;
//        }
        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillgoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setUserId(user.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);
        redisTemplate.opsForValue().set("order"+user.getId()+":"+goods.getId(), seckillOrder);
        return order;
    }

    @Override
    public OrderDetailVo getDetail(Long orderId) {
        Order order = baseMapper.selectById(orderId);
        Long goodsId = order.getGoodsId();
        GoodsVo goodsVo = goodsService.findGoodVoByGoodsId(goodsId);
        OrderDetailVo detail = new OrderDetailVo();
        detail.setOrder(order);
        detail.setGoodsVo(goodsVo);
        return detail;

    }
}
