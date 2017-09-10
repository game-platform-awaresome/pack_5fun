package com.wufan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by 7cc on 2017/9/7
 */

@EnableSwagger2
@SpringBootApplication
@EnableDiscoveryClient
public class PackDaemon {

    public static void main(String[] args){
        SpringApplication.run(PackDaemon.class,args);
    }
}
