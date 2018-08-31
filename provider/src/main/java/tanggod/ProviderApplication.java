package tanggod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tanggod.github.io.common.annotation.EnableFeignClientProxy;
import tanggod.github.io.common.annotation.EnableServerFallbackProxy;
import tanggod.github.io.runtimechangebytecode.core.SpringCloudBootstrap;

@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
@EnableServerFallbackProxy
//@EnableFeignClientProxy
public class ProviderApplication {

    public static void main(String[] args) throws Exception {
        SpringCloudBootstrap.run(ProviderApplication.class, args);
    }
}
