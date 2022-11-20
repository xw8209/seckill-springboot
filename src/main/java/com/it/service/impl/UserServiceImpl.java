package com.it.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.mapper.UserMapper;
import com.it.pojo.User;
import com.it.service.IUserService;
import com.it.utils.MD5Util;
import com.it.utils.ValidatorUtil;
import com.it.vo.LoginVo;
import com.it.vo.RespBean;
import com.it.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    @Autowired
    private UserMapper userMapper;
    /**
     * 登录
     * @param loginVo
     * @return
     */
    @Override
    public RespBean doLogin(LoginVo loginVo) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)) {
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        if(!ValidatorUtil.isMobile(mobile)) {
            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        }
        User user = userMapper.selectById(mobile);
        if(user == null) {
            return RespBean.error(RespBeanEnum.LOGIN_USERNAME_ERROR);
        }
        if(!MD5Util.formPassToDBPass(password,user.getSlat()).equals(user.getPasword()))
        {
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        return RespBean.success(RespBeanEnum.SUCCESS);
    }
}
