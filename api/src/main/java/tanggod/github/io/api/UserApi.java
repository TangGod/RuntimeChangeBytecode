package tanggod.github.io.api;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.ArrayList;
import java.util.List;
import tanggod.github.io.api.UserDto;
/*
 *
 *@author teddy
 *@date 2018/8/27
 */
@RequestMapping("/user")
@Proxy
public interface UserApi {


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    List<UserDto> list();

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    UserDto get();

    @RequestMapping(value = "/getById", method = RequestMethod.GET)
    UserDto getById(@RequestParam("id") String id);

    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    Boolean create(@RequestBody UserDto userDto);





    /*
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    default List<UserDto> list() {
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

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    default UserDto get() {
        return list().get(0);
    }

    @RequestMapping(value = "/getById", method = RequestMethod.GET)
    default UserDto getById(@RequestParam("id") String id) {
        return list().stream().filter(data -> data.getId().equals(id))
                .findFirst().orElse(null);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
    default Boolean create(@RequestBody UserDto userDto) {
        System.out.println(userDto.getAddress());
        System.out.println(userDto.getId());
        System.out.println(userDto.getIp());
        System.out.println(userDto.getName());
        System.out.println(userDto.getTelephone());
        return true;
    }
*/

}
