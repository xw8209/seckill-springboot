package com.it.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.pojo.Order;
import com.it.pojo.User;
import com.it.vo.GoodsVo;
import com.it.vo.OrderDetailVo;

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

    OrderDetailVo getDetail(Long orderId);

    String createPath(User user, Long goodsId);

    boolean checkPath(User user, Long goodsId, String path);
}
