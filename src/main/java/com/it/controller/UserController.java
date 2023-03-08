package com.it.controller;


import com.it.pojo.User;
import com.it.rabbitmq.MQSender;
import com.it.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jobob
 * @since 2022-11-19
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MQSender mqSender;
    //用户信息(测试)
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user){
        return RespBean.success(user);
    }

    /**
     * 厕所是发送RabbitMQ 消息
     */
//    @RequestMapping("/mq")
//    @ResponseBody
//    public void mq(){
//       mqSender.send("hello");
//    }
}
