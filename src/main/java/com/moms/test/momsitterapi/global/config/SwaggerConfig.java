package com.moms.test.momsitterapi.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "Bearer"
)
@OpenAPIDefinition(
        info =@Info(
                title = "Momsitter API",
                version = "${api.version}",
                contact = @Contact(
                        name = "David Roh", email = "white@gmail.com"
                ),
//                license = @License(
//                        name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"
//                ),
                termsOfService = "${tos.uri}",
                description = "${api.description}"
        )
//        ,
//        servers = @Server(
//                url = "${api.server.url}",
//                description = "Production"
//        )
)
public class SwaggerConfig {
//    @Bean
//    public OpenAPI openAPI(@Value("${demo.version}") String appVersion,
//                           @Value("${demo.url}") String url, @Value("${spring.profiles.active}") String active) {
//        Info info = new Info().title("Demo API - " + active).version(appVersion)
//                              .description("Spring Boot를 이용한 Demo 웹 애플리케이션 API입니다.")
//                              .termsOfService("http://swagger.io/terms/")
//                              .contact(new Contact().name("jini").url("https://blog.jiniworld.me/")
//                                                    .email("jini@jiniworld.me"))
//                              .license(new License().name("Apache License Version 2.0")
//                                                    .url("http://www.apache.org/licenses/LICENSE-2.0"));
//
//        List<Server> servers = Arrays.asList(new Server().url(url).description("demo (" + active + ")"));
//
//        SecurityScheme basichAuth = new SecurityScheme()
//                .type(SecurityScheme.Type.HTTP).scheme("basic");
//        SecurityRequirement securityItem = new SecurityRequirement().addList("basicAuth");
//
//        return new OpenAPI()
//                .components(new Components().addSecuritySchemes("basicAuth", basichAuth))
//                .addSecurityItem(securityItem)
//                .info(info)
//                .servers(servers);
//    }
}
