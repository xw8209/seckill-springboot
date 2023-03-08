package com.it.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.pojo.SeckillOrder;
import com.it.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2022-11-30
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    Long getResult(User user, Long goodsId);
}
