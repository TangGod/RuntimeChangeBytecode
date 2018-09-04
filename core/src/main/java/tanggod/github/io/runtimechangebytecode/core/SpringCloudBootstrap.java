package tanggod.github.io.runtimechangebytecode.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import tanggod.github.io.common.annotation.EnableFeignClientProxy;
import tanggod.github.io.common.annotation.EnableServerFallbackProxy;
import tanggod.github.io.common.annotation.EnableSpringMVCProxy;
import tanggod.github.io.common.utils.ServiceLoaderApi;
import tanggod.github.io.common.utils.SpringBeanUtils;
import tanggod.github.io.runtimechangebytecode.core.config.FeignConfig;
import tanggod.github.io.runtimechangebytecode.core.config.HystrixConfig;
import tanggod.github.io.runtimechangebytecode.core.config.SpringMVCConfig;

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
@SuppressWarnings("all")
public class SpringCloudBootstrap implements ApplicationBootstrap, ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanUtils.getInstance().setCfgContext((ConfigurableApplicationContext) applicationContext);
        runtimeChangeBytecodeList.stream().forEach(runtimeChangeBytecode -> SpringBeanUtils.getInstance().registerBean(runtimeChangeBytecode.getClass().getTypeName(), runtimeChangeBytecode));
    }

    private static List<? extends RuntimeChangeBytecode> runtimeChangeBytecodeList;

    public void run(Class<?> primarySource, String... args) throws Exception {
        long start = System.currentTimeMillis();
        loadSpiSupport(primarySource);
        initializeProxy(runtimeChangeBytecodeList);
        Double stop = Double.valueOf(String.valueOf(System.currentTimeMillis() - start)) / 1000;
        System.out.println("========================================== 生成代理类用时：" + stop + "/s ==========================================");
        SpringApplication.run(primarySource, args);
    }

    private void initializeProxy(List<? extends RuntimeChangeBytecode> runtimeChangeBytecodeList) throws Exception {
        RuntimeChangeBytecode feignConfig = runtimeChangeBytecodeList.stream().filter(runtimeChangeBytecode -> compareClassType(runtimeChangeBytecode, FeignConfig.class)).findFirst().orElse(null);
        if (null != feignConfig)
            feignConfig.createProxy(null, null);
        RuntimeChangeBytecode hystrixConfig = runtimeChangeBytecodeList.stream().filter(runtimeChangeBytecode -> compareClassType(runtimeChangeBytecode, HystrixConfig.class)).findFirst().orElse(null);
        if (null != hystrixConfig)
            hystrixConfig.createProxy(null, null);
        RuntimeChangeBytecode springMVCConfig = runtimeChangeBytecodeList.stream().filter(runtimeChangeBytecode -> compareClassType(runtimeChangeBytecode, SpringMVCConfig.class)).findFirst().orElse(null);
        if (null != springMVCConfig)
            springMVCConfig.createProxy(null, null);
    }

    private <S> void loadSpiSupport(Class<?> primarySource) {
        ServiceLoader<? extends RuntimeChangeBytecode> runtimeChangeBytecodes = ServiceLoaderApi.loadAll(RuntimeChangeBytecode.class);
        runtimeChangeBytecodeList = StreamSupport.stream(runtimeChangeBytecodes.spliterator(), false).filter(data -> (compareClassType(data, FeignConfig.class) ? primarySource.isAnnotationPresent(EnableFeignClientProxy.class) : true)).collect(Collectors.toList());
        runtimeChangeBytecodeList = runtimeChangeBytecodeList.stream().filter(data -> (compareClassType(data, HystrixConfig.class) ? primarySource.isAnnotationPresent(EnableServerFallbackProxy.class) : true)).collect(Collectors.toList());
        runtimeChangeBytecodeList = runtimeChangeBytecodeList.stream().filter(data -> (compareClassType(data, SpringMVCConfig.class) ? primarySource.isAnnotationPresent(EnableSpringMVCProxy.class) : true)).collect(Collectors.toList());
    }

    private boolean compareClassType(Object class1, Class class2) {
        return class1.getClass().getTypeName().equals(class2.getTypeName());
    }

}
