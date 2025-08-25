package com.kanha.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/")
    public String home(){
        return "...Radhe Radhe...";
    }

    @GetMapping("/api/test")
    public String test(){
        return "Testing Auth";
    }
}
