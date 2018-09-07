package tanggod;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tanggod.github.io.common.annotation.enable.EnableDependencyInjection;
import tanggod.github.io.common.annotation.enable.EnableServerFallbackProxy;
import tanggod.github.io.common.annotation.enable.EnableSpringMVCProxy;
import tanggod.github.io.runtimechangebytecode.core.ApplicationBootstrap;

@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
//@EnableFeignClientProxy(scanBasePackages = "tanggod")
@EnableServerFallbackProxy(scanBasePackages = "tanggod")
@EnableDependencyInjection(scanRestControllerBasePackages = "tanggod.github.io.provider.controller",scanServiceBasePackages = "tanggod.github.io.provider.service")
public class ProviderApplication {

    public static void main(String[] args) throws Exception {
        ApplicationBootstrap.run(ProviderApplication.class, args);
    }
}
