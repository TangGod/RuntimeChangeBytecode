package tanggod.github.io.common.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/*
 *Feign api 使用
 *@author teddy
 *@date 2018/8/31
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeignProxy {

    //provider application.name
    @AliasFor("name")
    String value();
}
