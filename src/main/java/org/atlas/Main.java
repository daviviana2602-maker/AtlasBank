package org.atlas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.out.println("Atlas Bank is Open");
    }

}