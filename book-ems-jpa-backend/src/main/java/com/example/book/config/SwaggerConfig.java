
package com.example.book.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI emsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Book EMS API (書本管理系統)")
                        .description("這是一個用於管理書本與出版社的 RESTful API。")
                        .version("v1.0.0"));
    }
}