package com.lj.cloudbox;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@MapperScan("com.lj.cloudbox.mapper")
public class CloudboxApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CloudboxApplication.class, args);
    }

}