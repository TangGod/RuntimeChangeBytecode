//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package tanggod.github.io.consumer.service;

import org.springframework.web.bind.annotation.RequestParam;
import tanggod.github.io.api.UserDto;
import tanggod.github.io.api.UserSpi;
import tanggod.github.io.common.annotation.ServerFallbackProxy;
import tanggod.github.io.common.dto.BaseBean;
import tanggod.github.io.common.dto.MessageBean;
import tanggod.github.io.common.utils.BaseService;
import tanggod.github.io.consumer.FallBack;
import tanggod.github.io.consumer.controller.UserApiConsumer;
import java.util.ArrayList;
import java.util.List;

@ServerFallbackProxy(fallbackSource = FallBack.class)
public class UserApiService extends BaseService {

    private UserApiConsumer t1;
    public UserApiConsumer t2;
    private UserApiConsumer t3;

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

    public void test1() {
        return ;
    }
    public BaseBean test2() {
        return null;
    }

    public MessageBean getById(@RequestParam("id") String id) {
        return null;
    }

    public MessageBean create(/*@*/ UserDto userDto) {
        System.out.println(userDto.getAddress());
        System.out.println(userDto.getId());
        System.out.println(userDto.getIp());
        System.out.println(userDto.getName());
        System.out.println(userDto.getTelephone());
        return this.result(true);
    }
}
