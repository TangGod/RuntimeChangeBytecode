package tanggod.github.io;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tanggod.github.io.api.UserApi;
import tanggod.github.io.api.UserDto;
import tanggod.github.io.common.annotation.EnableFeignClientProxy;
import tanggod.github.io.runtimechangebytecode.core.ApplicationBootstrap;
import tanggod.github.io.runtimechangebytecode.core.SpringCloudBootstrap;

import java.util.List;

@EnableFeignClients
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClientProxy
@RestController
public class ConsumerApplication {

	public static void main(String[] args) {
		ApplicationBootstrap.run(ConsumerApplication.class, args);
	}

	@Autowired(required = false)
	private UserApi userApi;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<UserDto> list() {
		List<UserDto> list = userApi.list().getData();
		return list;
	}
}
