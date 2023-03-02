package com.it.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.exception.GlobalException;
import com.it.mapper.UserMapper;
import com.it.pojo.User;
import com.it.service.IUserService;
import com.it.utils.CookieUtil;
import com.it.utils.MD5Util;
import com.it.utils.UUIDUtil;
import com.it.vo.LoginVo;
import com.it.vo.RespBean;
import com.it.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jobob
 * @since 2022-11-19
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Resource
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 登录
     *
     * @param loginVo
     * @param request
     * @param response
     * @return
     */
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
//        //参数校验
//        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)) {
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        }
//        if(!ValidatorUtil.isMobile(mobile)) {
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }
        //根据手机号获取用户
        User user = new User();
            user = userMapper.selectById(mobile);  

        
        if(user == null) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        // 判断密码是否正确
        if(!MD5Util.formPassToDBPass(password,user.getSalt()).equals(user.getPassword()))
        {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        // 生成cookie
        String ticket = UUIDUtil.uuid();
        redisTemplate.opsForValue().set("user:" + ticket,user);
        //request.getSession().setAttribute(ticket,user);
        CookieUtil.setCookie(request,response,"userTicket",ticket);
        return RespBean.success(ticket);
    }

    /**
     * 根据cookie获取用户
     * @param userTicket
     * @return
     */
    @Override
    public User getUserByCookie(String userTicket,HttpServletRequest request, HttpServletResponse response) {
        if(StringUtils.isEmpty(userTicket)){
            return null;
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        if(user != null){
            CookieUtil.setCookie(request,response,"userTicket",userTicket);
        }
        return user;
    }
}
