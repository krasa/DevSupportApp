package krasa.core.backend.config;

import org.springframework.context.annotation.*;

import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;

@Configuration
public class HystrixConfig {
	@Bean
	public HystrixCommandAspect hystrixAspect() {
		return new HystrixCommandAspect();
	}

}