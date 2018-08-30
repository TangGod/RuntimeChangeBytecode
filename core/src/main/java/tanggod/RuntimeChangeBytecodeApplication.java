package tanggod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tanggod.github.io.runtimechangebytecode.core.FeignConfig;
import tanggod.github.io.runtimechangebytecode.core.RuntimeChangeBytecode;

@EnableFeignClients
@SpringBootApplication
@EnableEurekaClient
public class RuntimeChangeBytecodeApplication {

	public static void main(String[] args) {
		RuntimeChangeBytecode config = new FeignConfig();
		try {
			config.createProxy("tanggod",config.getResolverSearchPath());
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		SpringApplication.run(RuntimeChangeBytecodeApplication.class, args);
	}
}
