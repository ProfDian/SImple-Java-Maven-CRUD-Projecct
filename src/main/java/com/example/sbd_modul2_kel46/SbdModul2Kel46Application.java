package com.example.sbd_modul2_kel46;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.*")
public class SbdModul2Kel46Application {

    
    public static void main(String[] args) {
        SpringApplication.run(SbdModul2Kel46Application.class, args);
    }
}