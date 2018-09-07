package tanggod.github.io.provider.controller;

import feign.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.List;

/*
 *
 *@author teddy
 *@date 2018/8/27
 */
@ServerFallbackProxy(fallbackSource = FallBack.class, supportGenerics = false)
public class UserController implements UserApi {

    private UserApiService userApiService;

    @Override
    public MessageBean list(String accessToken) {
        System.out.println(accessToken);
        return userApiService.list();
    }

    public MessageBean get() {
        return userApiService.get();
    }


    public MessageBean getById(String id) {
        return userApiService.getById(id);
    }


    public MessageBean create(UserDto userDto) {
        return userApiService.create(userDto);
    }
}
