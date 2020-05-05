package se.josef.cmsapi.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestHeader;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

/*
Visit gosef.se/api/swagger-ui.html to check and test endpoints
 */
@EnableSwagger2
@Configuration
public class SwaggerConfig {

    private final static String BASE_PACKAGE = "se.josef.cmsapi.resource";
    private final static String AUTHORIZATION_HEADER = "Authorization";
    private final static String PASS_AS = "header";
    private final static String AUTHORIZATION_SCOPE = "global";
    private final static String AUTHORIZATION_SCOPE_DESC = "accessEverything";

    @Value("${swagger.title}")
    private String title;
    @Value("${swagger.description}")
    private String description;
    @Value("${build.version}")
    private String version = "v1";
    @Value("${swagger.contact.name}")
    private String contactName;
    @Value("${swagger.contact.url}")
    private String contactUrl;
    @Value("${swagger.contact.email}")
    private String contactEmail;


    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(Lists.newArrayList(apiKey()))
                .securityContexts(Lists.newArrayList(securityContext()))
                .ignoredParameterTypes(RequestHeader.class)
                .apiInfo(apiInfo());
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.any())
                .build();
    }
    // Allows adding auth Bearer token in the ui, the actual security
    // settings for swagger doesn't really do anything.
    private ApiKey apiKey() {
        return new ApiKey(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER, PASS_AS);
    }


    private List<SecurityReference> defaultAuth() {

        AuthorizationScope[] authorizationScopes = {
                new AuthorizationScope(AUTHORIZATION_SCOPE, AUTHORIZATION_SCOPE_DESC)};

        return Lists.newArrayList(
                new SecurityReference(AUTHORIZATION_HEADER, authorizationScopes));
    }

    private ApiInfo apiInfo() {
        var contact = new Contact(contactName, contactUrl, contactEmail);
        return new ApiInfo(
                title,
                description,
                version,
                "",
                contact,
                "",
                "",
                Collections.emptyList());
    }

}
