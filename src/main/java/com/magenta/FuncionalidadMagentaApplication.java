package com.magenta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableJpaRepositories(basePackages = "com.magenta.persistence.repository")
@EntityScan(basePackages = "com.magenta.persistence.entity")
@SpringBootApplication(scanBasePackages = {"com.magenta"})
@EnableScheduling
public class FuncionalidadMagentaApplication {

    public static void main(String[] args) {
        SpringApplication.run(FuncionalidadMagentaApplication.class, args);
    }

}
