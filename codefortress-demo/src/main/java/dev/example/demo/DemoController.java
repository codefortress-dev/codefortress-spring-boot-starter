package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/public/hello")
    public String publicHello() {
        return "Hola, soy un endpoint público.";
    }

    @GetMapping("/private/secret")
    public String privateSecret() {
        return "TOP SECRET: Si lees esto, tienes Token válido y rol ADMIN.";
    }
}
