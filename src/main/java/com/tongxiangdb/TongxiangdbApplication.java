package com.tongxiangdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class TongxiangdbApplication {

    public static void main(String[] args) {
        SpringApplication.run(TongxiangdbApplication.class, args);
    }

    @Controller
    public class HomeController {
        @GetMapping("/")
        public String home() {
            return "index";
        }
    }
}
