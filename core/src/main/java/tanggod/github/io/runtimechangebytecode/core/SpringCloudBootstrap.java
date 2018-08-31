package tanggod.github.io.runtimechangebytecode.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import tanggod.github.io.common.annotation.EnableFeignClientProxy;
import tanggod.github.io.common.utils.ServiceLoaderApi;
import tanggod.github.io.common.utils.SpringBeanUtils;
import tanggod.github.io.runtimechangebytecode.core.config.FeignConfig;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/*
 *
 *@author teddy
 *@date 2018/8/30
 */
@Configurable
public class SpringCloudBootstrap implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanUtils.getInstance().setCfgContext((ConfigurableApplicationContext) applicationContext);
        SpringBeanUtils.getInstance().registerBean(runtimeChangeBytecode.getClass().getSimpleName(), runtimeChangeBytecode);
    }

    private static RuntimeChangeBytecode runtimeChangeBytecode;

    protected static void run(Class<?> primarySource, String... args) throws Exception {
        loadSpiSupport(primarySource);
        runtimeChangeBytecode.createProxy(null, null);
        SpringApplication.run(primarySource, args);
    }

    private static <S> void loadSpiSupport(Class<?> primarySource) {
        ServiceLoader<? extends RuntimeChangeBytecode> runtimeChangeBytecodes = ServiceLoaderApi.loadAll(RuntimeChangeBytecode.class);
        runtimeChangeBytecode = StreamSupport.stream(runtimeChangeBytecodes.spliterator(), false).findFirst().orElseThrow(NullPointerException::new);

        List<? extends RuntimeChangeBytecode> collect = StreamSupport.stream(runtimeChangeBytecodes.spliterator(), false).filter(data -> {
            if (data.getClass().getTypeName().equals(FeignConfig.class.getTypeName()) && !primarySource.isAnnotationPresent(EnableFeignClientProxy.class)) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

}
