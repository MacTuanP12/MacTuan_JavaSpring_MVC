package com.example.springmvc;

import org.springframework.context.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class SpringMvcApplication {
    public static void main(String[] args) {
        //container
        ApplicationContext abc = SpringApplication.run(SpringMvcApplication.class, args);
        for (String s : abc.getBeanDefinitionNames()) {
            System.out.println(s);
        }
    }
}