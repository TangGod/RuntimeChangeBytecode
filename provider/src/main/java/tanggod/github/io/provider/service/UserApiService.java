//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package tanggod.github.io.provider.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import tanggod.github.io.api.UserApi;
import tanggod.github.io.api.UserDto;
import tanggod.github.io.api.UserSpi;
import tanggod.github.io.common.annotation.Debug;
import tanggod.github.io.common.annotation.ServerFallbackProxy;
import tanggod.github.io.common.dto.BaseBean;
import tanggod.github.io.common.dto.MessageBean;
import tanggod.github.io.common.utils.BaseService;
import tanggod.github.io.provider.controller.UserController;

//@Service
@ServerFallbackProxy(
        resultType = MessageBean.class
)
//@Debug
public class UserApiService extends BaseService implements UserSpi {

    private UserController t1;
    public UserController t2;
    private UserController t3;

    public UserApiService() {
    }

    public MessageBean<List<UserDto>> list() {
        List<UserDto> result = new ArrayList();

        for(int i = 1; i <= 10; ++i) {
            UserDto userDto = new UserDto();
            userDto.setAddress("地址" + i);
            userDto.setId(String.valueOf(i));
            userDto.setIp("ip" + i);
            userDto.setName("name" + i);
            userDto.setTelephone("电话" + i);
            result.add(userDto);
        }

        return this.result(result);
    }

    public MessageBean get() {
        int i1 = 1 / 0;
        return this.result(((List)this.list().getData()).get(0));
    }

    public MessageBean fallback() {
        return this.result("请求错误");
    }

    public BaseBean getById(@RequestParam("id") String id) {
        return null;
    }

    public BaseBean create(/*@*/ UserDto userDto) {
        System.out.println(userDto.getAddress());
        System.out.println(userDto.getId());
        System.out.println(userDto.getIp());
        System.out.println(userDto.getName());
        System.out.println(userDto.getTelephone());
        return this.result(true);
    }
}
