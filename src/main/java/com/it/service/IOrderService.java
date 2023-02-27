package com.it.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.pojo.Order;
import com.it.pojo.User;
import com.it.vo.GoodsVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2022-11-30
 */
public interface IOrderService extends IService<Order> {

    Order seckill(User user, GoodsVo goodsVo);
}
