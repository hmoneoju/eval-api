package com.hmoneoju.evalapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class EvalApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EvalApiApplication.class, args);
	}
}
