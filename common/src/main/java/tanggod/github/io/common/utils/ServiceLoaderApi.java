package tanggod.github.io.common.utils;

import java.util.ServiceLoader;

/*
 *
 *@author teddy
 *@date 2018/8/31
 */
public class ServiceLoaderApi {

    public static <S> ServiceLoader<S> loadAll(final Class<S> clazz) {
        return ServiceLoader.load(clazz);
    }
}
