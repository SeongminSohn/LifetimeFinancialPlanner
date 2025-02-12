package com.app.lifetimefinancialplanner.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloContainer {
    @GetMapping("/api/hello")
    public String test() {
        return "Hello, world!";
    }
}
