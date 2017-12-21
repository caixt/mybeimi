package com.github.cxt.mybeimi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import com.github.cxt.mybeimi.web.handler.interceptor.CrossInterceptorHandler;
import org.springframework.scheduling.annotation.EnableAsync;


@EnableAutoConfiguration
@SpringBootApplication
@EnableAsync
public class MainApplication extends WebMvcConfigurerAdapter {
	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}
	
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new CrossInterceptorHandler()).addPathPatterns("/**");
        super.addInterceptors(registry);
	}

}
