package org.example.databaseservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DataBaseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataBaseServiceApplication.class, args);
    }

}
