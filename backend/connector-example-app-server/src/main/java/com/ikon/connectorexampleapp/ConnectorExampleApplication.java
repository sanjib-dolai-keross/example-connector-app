package com.ikon.connectorexampleapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.ikon.connector",
        "com.ikon.connectorexampleapp"
})
public class ConnectorExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConnectorExampleApplication.class, args);

    }
}
