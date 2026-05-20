package com.e0bmanager.server_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController // Thêm dòng này
public class ServerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApiApplication.class, args);
    }

    @GetMapping("/hello") // Thử gõ: http://localhost:8080/hello
    public String hello() {
        return "Server is working!";
    }
}
