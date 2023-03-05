package com.it.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.it.pojo.Order;
import com.it.pojo.SeckillOrder;
import com.it.pojo.User;
import com.it.service.IGoodsService;
import com.it.service.IOrderService;
import com.it.service.ISeckillOrderService;
import com.it.vo.GoodsVo;
import com.it.vo.RespBean;
import com.it.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/seckill")
public class SecKillController {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private IOrderService orderService;

    /**
     * 优化前QPS:444
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/doSeckill2")
    public String doSeckill(Model model, User user, Long goodsId){
        if(user == null){
            return "login";
        }
        model.addAttribute("user",user);
        GoodsVo goods = goodsService.findGoodVoByGoodsId(goodsId);
        //判断库存
        if(goods.getGoodsStock() < 1){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "seckillFail";
        }
        //判断是否重复抢购
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if(seckillOrder != null){
            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
            return "seckillFail";
        }
        Order order = orderService.seckill(user,goods);
        model.addAttribute("order",order);
        model.addAttribute("goods",goods);
        return "orderDetail";
    }

    @RequestMapping(value = "/doSeckill",method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(User user, Long goodsId){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        GoodsVo goods = goodsService.findGoodVoByGoodsId(goodsId);
        //判断库存
        if(goods.getGoodsStock() < 1){

            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //判断是否重复抢购
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if(seckillOrder != null){
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }
        Order order = orderService.seckill(user,goods);
        return RespBean.success(order);
    }
}