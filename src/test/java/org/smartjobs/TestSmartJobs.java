package org.smartjobs;

import org.smartjobs.config.TestcontainersConfiguration;
import org.springframework.boot.SpringApplication;

public class TestSmartJobs {

    public static void main(String[] args) {
        SpringApplication
                .from(SmartJobs::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}