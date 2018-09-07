package tanggod.github.io.common.annotation.enable;

import java.lang.annotation.*;

/*
 *注解在启动类上则会在运行期间生产@FeignClient的代理
 *@author teddy
 *@date 2018/8/31
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableFeignClientProxy {
    //是否使用创建新的代理类方式
    boolean enableCreateNewProxyClass() default true;

    //扫包
    String[] scanBasePackages();
}
