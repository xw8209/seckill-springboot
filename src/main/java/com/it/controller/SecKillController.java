package com.it.controller;


import cloud.tianai.captcha.spring.annotation.Captcha;
import cloud.tianai.captcha.spring.request.CaptchaRequest;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.it.pojo.Order;
import com.it.pojo.SeckillMessage;
import com.it.pojo.SeckillOrder;
import com.it.pojo.User;
import com.it.rabbitmq.MQSender;
import com.it.service.IGoodsService;
import com.it.service.IOrderService;
import com.it.service.ISeckillOrderService;
import com.it.vo.GoodsVo;
import com.it.vo.RespBean;
import com.it.vo.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DefaultRedisScript script;
    @Autowired
    private MQSender mqSender;

    private Map<Long,Boolean> EmptyStockMap = new HashMap<>();
    /**
     * 优化前QPS:444
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping("/doSeckill2")
    public String doSeckill(Model model, User user, Long goodsId) {

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

    /**
     * 优化前QPS:444
     * 优化后QS: 1319
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/{path}/doSeckill",method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(@PathVariable String path, User user, Long goodsId){

            if (user == null) {
                return RespBean.error(RespBeanEnum.SESSION_ERROR);
            }
            ValueOperations valueOperations = redisTemplate.opsForValue();
            //判断路径
            boolean check = orderService.checkPath(user, goodsId, path);

            if (!check) {
                return RespBean.error(RespBeanEnum.PATH_ERROR);
            }
            //判断是否重复抢购
            SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
            if (seckillOrder != null) {
                return RespBean.error(RespBeanEnum.REPEAT_ERROR);
            }
            //通过内存标记减少redis访问
            if (EmptyStockMap.get(goodsId)) {
                return RespBean.error(RespBeanEnum.REPEAT_ERROR);
            }
            //预减库存
            //Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
            Long stock = (Long) redisTemplate.execute(script, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);
            if (stock < 0) {
                EmptyStockMap.put(goodsId, true);
                valueOperations.increment("seckillGoods:" + goodsId);
                return RespBean.error(RespBeanEnum.EMPTY_STOCK);
            }
            SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
            mqSender.sendSeckillMessage(JSON.toJSONString(seckillMessage));
            return RespBean.success(0);
    }
    //获取秒杀的接口
    //@Captcha("SLIDER")
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, HttpServletRequest request) {
        if(user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 限制访问次数，5秒内访问5次
        String uri = request.getRequestURI();
        Integer count = (Integer) valueOperations.get(uri + ":"+user.getId());
        if(count == null){
            valueOperations.set(uri + ":" + user.getId(),1,5, TimeUnit.SECONDS);
        }else if(count < 5){
            valueOperations.increment(uri + ":"+user.getId());
        }else{
            return RespBean.error(RespBeanEnum.ACCESS_ERROR);
        }
        //判断验证码是否正确
//        Boolean check = orderService.checkCptcha(user, goodsId, captcha);
//        if(!check) {
//            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
//        }

        //生成秒杀的接口
        String str = orderService.createPath(user, goodsId);
        return RespBean.success(str);
    }
    /**
     * 获取秒杀结果
     * return：orderId：成功 -1：失败： 0：排队中
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user,Long goodsId){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user,goodsId);
        return RespBean.success(orderId);
    }




    /**
     * 初始化,减库存就可以绕过数据库，直接在redis上操作
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodVo();
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        list.forEach(goodsVo ->{
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(),goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(),false);
        });
    }
}
