package com.it.vo;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    //common
    SUCCESS(200,"SUCCESS"),
    ERROR(500,"服务端异常"),
    //login
    LOGIN_USERNAME_ERROR(500210,"未找到该用户"),
    LOGIN_ERROR(500210,"用户名或密码为空"),
    MOBILE_ERROR(500211,"手机号码不正确"),
    BIND_ERROR(500212,"参数校验异常"),
    //秒杀模块
    EMPTY_STOCK(500500,"空库存"),
    REPEAT_ERROR(500501,"每人限购一件"),
    ;
    private final Integer code;
    private final String message;
}
