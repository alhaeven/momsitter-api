package com.moms.test.momsitterapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackageClasses = MomsitterApiApplication.class)
public class MomsitterApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MomsitterApiApplication.class, args);
	}

}
