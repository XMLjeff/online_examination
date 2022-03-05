package com.project.online_examination;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.project"})

public class OnlineExaminationApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineExaminationApplication.class, args);
    }

}
