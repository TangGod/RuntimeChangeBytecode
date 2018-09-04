package tanggod.github.io.common.type;

import java.util.HashMap;
import java.util.Map;

/*
 *
 *@author teddy
 *@date 2018/9/3
 */
public interface ApplicationCache {

    //service
    public static final Map<String, String> proxyService = new HashMap<>(); // key：可以注入的classTypeName  value： invoke的方法名

    public static final Map<String, Object> invoker = new HashMap<>(); // key：可以注入的classTypeName  value： invoke的class

}
