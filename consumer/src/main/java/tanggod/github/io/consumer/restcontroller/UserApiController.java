package tanggod.github.io.consumer.restcontroller;

import tanggod.github.io.api.UserApi;
import tanggod.github.io.api.UserDto;
import tanggod.github.io.common.annotation.ServerFallbackProxy;
import tanggod.github.io.common.dto.MessageBean;
import tanggod.github.io.consumer.FallBack;
import tanggod.github.io.consumer.service.UserApiService;

import java.util.List;

/*
 *
 *@author teddy
 *@date 2018/9/4
 */
@ServerFallbackProxy(fallbackSource = FallBack.class, supportGenerics = false/*,component = RestController.class*/)
public class UserApiController {

    private UserApi userApi;

    private UserApiService userApiService;

    public MessageBean getList() {
        MessageBean<List<UserDto>> list = userApi.list("123");
        return list;
    }

    public MessageBean getGet() {
        MessageBean messageBean = userApi.get();
        return messageBean;
    }

    public MessageBean getById(String id) {
        return userApiService.getById(id);
    }

    public MessageBean postCreate(UserDto userDto) {
        return userApi.create(userDto);
    }
}
