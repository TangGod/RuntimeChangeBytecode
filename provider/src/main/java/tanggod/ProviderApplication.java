package tanggod;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tanggod.github.io.common.annotation.EnableFeignClientProxy;
import tanggod.github.io.common.annotation.EnableServerFallbackProxy;
import tanggod.github.io.common.annotation.EnableSpringMVCProxy;
import tanggod.github.io.runtimechangebytecode.core.ApplicationBootstrap;
import tanggod.github.io.runtimechangebytecode.core.SpringCloudBootstrap;

@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
@EnableServerFallbackProxy
//@EnableFeignClientProxy
@EnableSpringMVCProxy
public class ProviderApplication {

    public static void main(String[] args) throws Exception {
        ApplicationBootstrap.run(ProviderApplication.class, args);
    }
}
