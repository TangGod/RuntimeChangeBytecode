package tanggod.github.io.common.annotation;

import java.lang.annotation.*;

/*
 *注解在启动类上则会在运气期间生产@FeignClient的代理
 *@author teddy
 *@date 2018/8/31
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableFeignClientProxy {
}
