package com.example.springmvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ApplicationContext;
// include = bao gồm >< exclude= loại bỏ ra
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class SpringMvcApplication {

    public static void main(String[] args) {
        //container
        ApplicationContext abc = SpringApplication.run(SpringMvcApplication.class, args);
        for (String s : abc.getBeanDefinitionNames()) {
            System.out.println(s);
        }
    }

}
