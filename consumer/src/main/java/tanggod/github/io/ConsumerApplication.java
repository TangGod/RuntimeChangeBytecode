package tanggod.github.io;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tanggod.github.io.api.UserApi;
import tanggod.github.io.api.UserDto;
import tanggod.github.io.common.annotation.EnableFeignClientProxy;
import tanggod.github.io.common.annotation.EnableServerFallbackProxy;
import tanggod.github.io.common.annotation.EnableSpringMVCProxy;
import tanggod.github.io.runtimechangebytecode.core.ApplicationBootstrap;
import tanggod.github.io.runtimechangebytecode.core.SpringCloudBootstrap;

import java.util.List;

@EnableFeignClients
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClientProxy
@EnableServerFallbackProxy
@EnableCircuitBreaker
//@EnableSpringMVCProxy
public class ConsumerApplication {

	public static void main(String[] args) {
		ApplicationBootstrap.run(ConsumerApplication.class, args);
	}
}
