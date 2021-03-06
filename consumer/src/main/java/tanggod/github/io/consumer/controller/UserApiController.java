package tanggod.github.io.consumer.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    public String getListView() {
        return "test";
    }
}
