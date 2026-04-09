package com.example.book.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI emsOpenAPI() {
        // ✅ 修正：改成全小寫的 bearerAuth
        String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Book EMS API (書本管理系統)")
                        .description("這是一個用於管理書本與出版社的 RESTful API，已全面升級支援 JWT 身分驗證。")
                        .version("v1.0.0"))
                // 1. 告訴 Swagger 所有的 API 預設都需要經過名為 "bearerAuth" 的驗證
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                // 2. 定義 "bearerAuth" 是什麼（HTTP Bearer Token 格式）
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}