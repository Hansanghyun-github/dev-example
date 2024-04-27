package com.example.devexample;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@MapperScan(value = {"com.example.devexample.dev"})
public class DevExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevExampleApplication.class, args);
	}

}
