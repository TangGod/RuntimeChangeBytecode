package tanggod.github.io.consumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tanggod.github.io.api.UserApi;
import tanggod.github.io.api.UserDto;
import tanggod.github.io.common.annotation.ServerFallbackProxy;
import tanggod.github.io.common.dto.MessageBean;
import tanggod.github.io.consumer.FallBack;

import java.util.List;

/*
 *
 *@author teddy
 *@date 2018/9/4
 */
@ServerFallbackProxy(fallbackSource = FallBack.class,component = RestController.class)
public class UserApiConsumer {

    private UserApi userApi;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public MessageBean list() {
        MessageBean<List<UserDto>> list = userApi.list();
        return list;
    }

}
