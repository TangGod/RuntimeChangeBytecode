package tanggod.github.io.provider;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import tanggod.github.io.api.UserApi;
import tanggod.github.io.api.UserDto;
import tanggod.github.io.common.dto.BaseBean;
import tanggod.github.io.common.dto.MessageBean;
import tanggod.github.io.common.utils.BaseService;

import java.util.List;

/*
 *
 *@author teddy
 *@date 2018/8/27
 */
@RestController
public class UserController implements UserApi{

    @Autowired
    private UserApiService userApi;

    @Override
    public MessageBean<List<UserDto>> list() {
        return userApi.list();
    }

    @Override
    public BaseBean get() {
        return userApi.get();
    }

    @Override
    public BaseBean getById(String id) {
        return userApi.getById(id);
    }

    @Override
    public BaseBean create(UserDto userDto) {
        return userApi.create(userDto);
    }
}
