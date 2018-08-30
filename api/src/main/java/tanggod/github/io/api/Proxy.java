package tanggod.github.io.api;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Proxy {

    //provider application.name
    @AliasFor("name")
    String value();
}
