package com.mokasong.config.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {
    @Value("${swagger.api-version}")
    private String API_VERSION;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.mokasong.controller")) // API 목록이 나타날 패키지
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .securitySchemes(apiKey()); // JWT 인증을 위한 header 설정
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Mokasong API")
                .description("Mokasong 서비스 API (www.mokasong.com)")
                .version(API_VERSION)
                .build();
    }

    private List<SecurityScheme> apiKey() {
        List<SecurityScheme> list = new ArrayList<>();
        list.add(new ApiKey("Authorization", "Authorization", "header"));
        return list;
    }
}
