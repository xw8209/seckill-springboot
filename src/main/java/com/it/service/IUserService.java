package com.it.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.pojo.User;
import com.it.vo.LoginVo;
import com.it.vo.RespBean;

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
     * @param loginVo
     * @return
     */
    RespBean doLogin(LoginVo loginVo);
}
