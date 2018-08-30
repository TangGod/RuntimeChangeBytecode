package tanggod.github.io.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import tanggod.github.io.api.UserApi;
import tanggod.github.io.api.UserDto;

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
    public List<UserDto> list() {
        return userApi.list();
    }

    @Override
    public UserDto get() {
        return userApi.get();
    }

    @Override
    public UserDto getById(String id) {
        return userApi.getById(id);
    }

    @Override
    public Boolean create(UserDto userDto) {
        return userApi.create(userDto);
    }
}
