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
    MOBILE_ERROR(500220,"手机号码不正确");
    private final Integer code;
    private final String message;
}
