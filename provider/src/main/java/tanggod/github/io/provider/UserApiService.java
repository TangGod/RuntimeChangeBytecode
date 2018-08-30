package tanggod.github.io.provider;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import tanggod.github.io.api.UserApi;
import tanggod.github.io.api.UserDto;

import java.util.ArrayList;
import java.util.List;

/*
 *
 *@author teddy
 *@date 2018/8/27
 */
@Service
public class UserApiService {

    public List<UserDto> list() {
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
        return result;
    }

    public UserDto get() {
        return list().get(0);
    }

    public UserDto getById(@RequestParam("id") String id) {
        return list().stream().filter(data -> data.getId().equals(id))
                .findFirst().orElse(null);
    }

    public Boolean create(@RequestBody UserDto userDto) {
        System.out.println(userDto.getAddress());
        System.out.println(userDto.getId());
        System.out.println(userDto.getIp());
        System.out.println(userDto.getName());
        System.out.println(userDto.getTelephone());
        return true;
    }
}
