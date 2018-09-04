package tanggod.github.io.provider.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import tanggod.github.io.api.UserApi;
import tanggod.github.io.api.UserDto;
import tanggod.github.io.api.UserSpi;
import tanggod.github.io.common.annotation.Debug;
import tanggod.github.io.common.dto.BaseBean;
import tanggod.github.io.common.dto.MessageBean;
import tanggod.github.io.provider.service.UserApiService;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.List;

/*
 *
 *@author teddy
 *@date 2018/8/27
 */
//@RestController
//@Debug
public class UserController implements UserApi{

    //private UserApiService userApi;

    //@Autowired(required = false)
    private UserSpi userSpi;

    @Override
    public MessageBean<List<UserDto>> list() {
        return userSpi.list();
    }

    @Override
    public MessageBean get() {
        return userSpi.get();
    }

    @Override
    public BaseBean getById(String id) {
        return userSpi.getById(id);
    }

    @Override
    public BaseBean create(UserDto userDto) {
        return userSpi.create(userDto);
    }
}
