package com.example.devexample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/hello")
    public String getHello(){
        return "Hello World";
    }

    @GetMapping("/health")
    public void healthCheck(){
        log.debug("health check");
    }
}
