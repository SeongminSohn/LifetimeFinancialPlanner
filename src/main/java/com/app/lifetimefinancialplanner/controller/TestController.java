package com.app.lifetimefinancialplanner.controller;


import com.app.lifetimefinancialplanner.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:5173")

public class TestController {
    @Autowired
    private TestService testService;
    public TestController(TestService testService){
        this.testService = testService;
        System.out.println("TestController");
    }
    @GetMapping("/test")
    public String test(){
        System.out.println("testing....");
        return testService.serviceTesting();

    }
}
