package tanggod.github.io.runtimechangebytecode.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tanggod.github.io.api.UserApi;
import tanggod.github.io.api.UserDto;

import java.util.List;

/*
 *
 *@author teddy
 *@date 2018/8/27
 */
//@RestController
public class ConsumerUserController {

    @Autowired(required = false)
    private UserApi userApi;


    public ConsumerUserController(){
        System.out.println();
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<UserDto> list() {
        List<UserDto> list = userApi.list().getData();
        return list;
    }

}
