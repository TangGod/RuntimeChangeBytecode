package tanggod.github.io.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import tanggod.github.io.common.dto.BaseBean;
import tanggod.github.io.common.dto.MessageBean;

import java.util.List;

/*
 *
 *@author teddy
 *@date 2018/9/4
 */
public interface UserSpi {
    MessageBean<List<UserDto>> list();

    MessageBean get();

    MessageBean getById(@RequestParam("id") String id);

    MessageBean create(@RequestBody UserDto userDto);
}
