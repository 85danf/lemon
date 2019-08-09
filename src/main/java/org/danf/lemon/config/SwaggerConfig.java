package org.danf.lemon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

/**
 * Swagger documentation endpoint configuration.
 *
 * @author Dan Feldman
 */
@Configuration
@EnableSwagger2
@Component
@Primary
public class SwaggerConfig {

    @Bean
    public Docket swaggerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.danf.lemon"))
                .paths(PathSelectors.any())
                .build()
                .enable(true)
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        Contact contactInfo = new Contact("Dan Feldman", "https://github.com/feldan", "85danf@gmail.com");
        return new ApiInfo(
                "Dan's coding task",
                "",
                "1.0.0",
                "",
                contactInfo,
                "MIT",
                "",
                new ArrayList<>());
    }

}
