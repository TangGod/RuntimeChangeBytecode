package tanggod.github.io.common.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServerFallbackProxy {

    //返回值类型
    //Class resultType();

    //返回值的数据源
    Class fallbackSource();

    //数据源中的获取返回值的方法名
    String methodName() default "result";

    //触发服务降级时，返回的值  此方法的返回值必须与原方法一致
    String fallbackValue() default "系统繁忙，请稍后再试";

    //最大并发数 如果并发数达到该设置值，请求会被拒绝和抛出异常并且fallback不会被调用。
    int maxConcurrent() default 15;
}
