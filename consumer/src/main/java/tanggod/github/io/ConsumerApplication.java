package tanggod.github.io;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tanggod.github.io.common.annotation.enable.EnableDependencyInjection;
import tanggod.github.io.common.annotation.enable.EnableFeignClientProxy;
import tanggod.github.io.common.annotation.enable.EnableServerFallbackProxy;
import tanggod.github.io.common.annotation.enable.EnableSpringMVCProxy;
import tanggod.github.io.runtimechangebytecode.core.ApplicationBootstrap;

@EnableFeignClients
@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
@EnableFeignClientProxy(scanBasePackages = "tanggod")
@EnableServerFallbackProxy(scanBasePackages = "tanggod")
@EnableSpringMVCProxy(scanRequestMappingBasePackages ={"tanggod.github.io.consumer.controller","tanggod.github.io.consumer.restcontroller"} )
@EnableDependencyInjection(scanServiceBasePackages = "tanggod.github.io.consumer.service",
        scanRestControllerBasePackages = "tanggod.github.io.consumer.restcontroller"
        ,scanControllerBasePackages = "tanggod.github.io.consumer.controller"
        )
public class ConsumerApplication {

    public static void main(String[] args) {
        ApplicationBootstrap.run(ConsumerApplication.class, args);
    }


}
