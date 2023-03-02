package com.it.controller;

import com.it.pojo.User;
import com.it.service.IGoodsService;
import com.it.service.IUserService;
import com.it.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
public class GoodsController {
@Autowired
private IUserService userService;
@Autowired
private IGoodsService goodsService;

@Autowired
private RedisTemplate redisTemplate;

@Autowired
private ThymeleafViewResolver viewResolver;

    /**
     * windows 优化前 562
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(value = "/toList",produces = "text/html;charset = utf-8")
    public String toList(Model model, User user, HttpServletRequest request, HttpServletResponse response){
        //Redis中获取页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        //如果不为空直接返回
        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
            return html;
        }
        //否则手动渲染
//        if(user == null){
//            return "login";
//        }
        model.addAttribute("user",user);
        model.addAttribute("goodsList",goodsService.findGoodVo());
        //return "goodsList";
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(),model.asMap());
        String newHtml = viewResolver.getTemplateEngine().process("goodsList", webContext);
        if(!StringUtils.isEmpty(newHtml)) {
            valueOperations.set("goodsList", newHtml, 60, TimeUnit.SECONDS);
        }
        return newHtml;

    }

    /**
     * 跳转到商品列表页面
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(value ="/toDetail/{goodsId}", produces = "text/html;charset=utf-8")
    public String toDetail(Model model, User user, @PathVariable Long goodsId, HttpServletRequest request, HttpServletResponse response){
        //redis中获取页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsDetail:" + goodsId);
        if(!StringUtils.isEmpty(html)) {
            return html;
        }
        model.addAttribute("user",user);
        GoodsVo goodsVo = goodsService.findGoodVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        int secKillStatus = 0;
        int remainSeconds = 0;
        //秒杀还未开始
        if(nowDate.before(startDate)){
            secKillStatus = 0;
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime())/1000);
        }else if(nowDate.after(endDate)){
            //秒杀结束
            secKillStatus = 2;
            remainSeconds = -1;
        }else{
            //秒杀进行中
            secKillStatus = 1;
            remainSeconds = 0;
        }

        model.addAttribute("goods",goodsVo);
        model.addAttribute("secKillStatus",secKillStatus);
        model.addAttribute("remainSeconds",remainSeconds);
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(),model.asMap());
        String newHtml = viewResolver.getTemplateEngine().process("goodsDetail", webContext);
        if(!StringUtils.isEmpty(newHtml)) {
            valueOperations.set("goodsDetail:"+goodsId, newHtml, 60, TimeUnit.SECONDS);
        }
        return newHtml;
    }
}
