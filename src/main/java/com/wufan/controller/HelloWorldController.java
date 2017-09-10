package com.wufan.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 7cc on 2017/9/7
 */

@RestController
public class HelloWorldController {

    @GetMapping("/hello")
    public String helloWorld(){
        return "hello cc";
    }
}
