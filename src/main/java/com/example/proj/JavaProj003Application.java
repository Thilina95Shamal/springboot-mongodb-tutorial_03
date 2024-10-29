package com.example.proj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class JavaProj003Application {

    public static void main(String[] args) {
        SpringApplication.run(JavaProj003Application.class, args);
    }

}
