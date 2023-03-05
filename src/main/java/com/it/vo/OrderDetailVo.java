package com.it.vo;


import com.it.pojo.Order;
import lombok.Data;

@Data
public class OrderDetailVo {

    private Order order;
    private GoodsVo goodsVo;
}