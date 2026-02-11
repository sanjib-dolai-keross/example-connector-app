package com.ikon.dacexampleapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.ikon.autoconfigure.annotation.EnableIkonSdk;
import com.ikon.sdk.config.IkonSdkConfig;

@EntityScan(basePackages = { "com.ikon.dacexampleapp.entity" })
@EnableJpaRepositories(basePackages = { "com.ikon.dacexampleapp.repository" })
@EnableIkonSdk(configuration = IkonSdkConfig.class)
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.ikon.dacexampleapp",
        "com.ikon.sdk"
})
public class DacExampleAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(DacExampleAppApplication.class, args);
    }
}