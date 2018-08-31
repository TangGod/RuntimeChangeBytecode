package tanggod.github.io.provider;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import tanggod.github.io.api.UserDto;
import tanggod.github.io.common.annotation.ServerFallbackProxy;
import tanggod.github.io.common.dto.BaseBean;
import tanggod.github.io.common.dto.MessageBean;
import tanggod.github.io.common.utils.BaseService;

import java.util.ArrayList;
import java.util.List;

/*
 *
 *@author teddy
 *@date 2018/8/27
 */
@Service
@ServerFallbackProxy(resultType = MessageBean.class)
public class UserApiService extends BaseService{

    public MessageBean<List<UserDto>> list() {
        List<UserDto> result = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            UserDto userDto = new UserDto();
            userDto.setAddress("地址" + i);
            userDto.setId(String.valueOf(i));
            userDto.setIp("ip" + i);
            userDto.setName("name" + i);
            userDto.setTelephone("电话" + i);
            result.add(userDto);
        }
        return result(result);
    }

  /*  @HystrixCommand(fallbackMethod = "fallback",
            commandProperties = {
                    //默认10秒;如果并发数达到该设置值，请求会被拒绝和抛出异常并且fallback不会被调用。
                    @HystrixProperty(name= HystrixPropertiesManager.FALLBACK_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS, value="15")
            })*/
    //覆写fallbackMethod中指定的方法，注意，此方法的返回值，参数必须与原方法一致
    public MessageBean get() {
        int i1 = 1 /0 ;
        return result(list().getData().get(0));
    }

    public BaseBean getById(@RequestParam("id") String id) {
        return result(list().getData().stream().filter(data -> data.getId().equals(id))
                .findFirst().orElse(null));
    }

    public BaseBean create(@RequestBody UserDto userDto) {
        System.out.println(userDto.getAddress());
        System.out.println(userDto.getId());
        System.out.println(userDto.getIp());
        System.out.println(userDto.getName());
        System.out.println(userDto.getTelephone());
        return result(true);
    }

}
