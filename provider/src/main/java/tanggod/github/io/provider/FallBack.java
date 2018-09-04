package tanggod.github.io.provider;

import tanggod.github.io.common.annotation.ServerFallbackProxy;
import tanggod.github.io.common.dto.MessageBean;
import tanggod.github.io.common.type.FallbackSource;
import tanggod.github.io.provider.service.UserApiService;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/*
 *
 *@author teddy
 *@date 2018/9/4
 */
public class FallBack implements FallbackSource {

    private static MessageBean result = new MessageBean();

    static {
        result.setMeta("操作的太快,请重试");
    }

    @Override
    public MessageBean result() {
        return result;
    }

}
