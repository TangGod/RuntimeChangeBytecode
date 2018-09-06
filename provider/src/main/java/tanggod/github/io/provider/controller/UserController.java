package tanggod.github.io.provider.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import tanggod.github.io.api.UserApi;
import tanggod.github.io.api.UserDto;
import tanggod.github.io.api.UserSpi;
import tanggod.github.io.common.annotation.Debug;
import tanggod.github.io.common.annotation.ServerFallbackProxy;
import tanggod.github.io.common.dto.BaseBean;
import tanggod.github.io.common.dto.MessageBean;
import tanggod.github.io.provider.FallBack;
import tanggod.github.io.provider.service.UserApiService;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.List;

/*
 *
 *@author teddy
 *@date 2018/8/27
 */
@ServerFallbackProxy(fallbackSource = FallBack.class)
public class UserController implements UserApi{

    //private UserApiService userApi;

    //@Autowired(required = false)
    private UserApiService userApiService;

    @Override
    public MessageBean<List<UserDto>> list() {
        return userApiService.list();
    }

    @Override
    public MessageBean get() {
        return userApiService.get();
    }

    @Override
    public MessageBean getById(String id) {
        return userApiService.getById(id);
    }

    @Override
    public MessageBean create(UserDto userDto) {
        return userApiService.create(userDto);
    }
}
