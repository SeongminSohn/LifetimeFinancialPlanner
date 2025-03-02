package com.app.lifetimefinancialplanner.service;

import org.springframework.stereotype.Service;

@Service
public class TestService {
    public String serviceTesting(){
        System.out.println("This is Test Service function");
        return "Test Service function";
    }
}
