package com.it.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.pojo.User;
import com.it.vo.LoginVo;
import com.it.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2022-11-19
 */
public interface IUserService extends IService<User> {

    /**
     * 登录
     *
     * @param loginVo
     * @param request
     * @param response
     * @return
     */
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);
}
