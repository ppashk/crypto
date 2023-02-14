package com.crypto;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
@EnableJpaAuditing
@EntityScan(basePackageClasses = {Application.class, Jsr310JpaConverters.class})
public class Application implements WebMvcConfigurer {

    public static void main(String[] args) {
        run(Application.class, args);
    }
}
