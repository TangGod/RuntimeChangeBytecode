package tanggod;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tanggod.github.io.common.annotation.EnableFeignClientProxy;
import tanggod.github.io.runtimechangebytecode.core.ApplicationBootstrap;
import tanggod.github.io.runtimechangebytecode.core.SpringCloudBootstrap;

@EnableFeignClients
@SpringBootApplication
@EnableEurekaClient

@EnableCircuitBreaker //开启服务降级 断路器
@EnableHystrix
public class RuntimeChangeBytecodeApplication   {

	public static void main(String[] args) throws Exception {
		ApplicationBootstrap.run(RuntimeChangeBytecodeApplication.class,args);
	}
}
