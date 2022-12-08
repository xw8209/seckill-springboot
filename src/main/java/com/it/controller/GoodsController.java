package com.it.controller;

import com.it.pojo.User;
import com.it.service.IGoodsService;
import com.it.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/goods")
public class GoodsController {
@Autowired
private IUserService userService;
@Autowired
private IGoodsService goodsService;
    @RequestMapping("/toList")
    public String toList(Model model, User user){
        if(user == null){
            return "login";
        }
        model.addAttribute("user",user);
        model.addAttribute("goodsList",goodsService.findGoodVo());
        return "goodsList";
    }

    /**
     * 跳转到商品列表页面
     * @param model
     * @param user
     * @return
     */
    @RequestMapping("/toDetail/{goodsId}")
    public String toDetail(Model model, User user, @PathVariable Long GoodsId){
        model.addAttribute("user",user);
        model.addAttribute("goods",goodsService.findGoodVoByGoodsId(GoodsId));
        return "goodsDetail";
    }
}
