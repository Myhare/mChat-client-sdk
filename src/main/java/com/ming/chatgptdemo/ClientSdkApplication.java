package com.ming.chatgptdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@SpringBootApplication
public class ClientSdkApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientSdkApplication.class, args);
    }
}
