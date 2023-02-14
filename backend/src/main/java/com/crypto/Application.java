package com.crypto;

import com.crypto.utils.annotation.CurrentUserIdResolver;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
@EnableJpaAuditing
@EnableWebMvc
@Configuration
@EntityScan(basePackageClasses = {Application.class, Jsr310JpaConverters.class})
public class Application implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new CurrentUserIdResolver());
    }

    public static void main(String[] args) {
        run(Application.class, args);
    }
}
