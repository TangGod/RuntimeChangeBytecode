package tanggod.github.io.common.annotation.enable;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableDependencyInjection {

    //是否使用创建新的代理类方式
    boolean enableCreateNewProxyClass() default false;

    String[] scanServiceBasePackages() default "undefined";
    String[] scanRestControllerBasePackages()  default "undefined";
    String[] scanControllerBasePackages()  default "undefined";
}
