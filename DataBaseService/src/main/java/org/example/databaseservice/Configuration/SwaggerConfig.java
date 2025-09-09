package org.example.databaseservice.Configuration;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {


    @Value("${server.port}")
    private Integer port;


    @Bean
    public OpenAPI customOpenAPI() {
        Info info = new Info();
        String url = "http://localhost:"+port;
        info.title("Database Service API");
        return new OpenAPI().servers(List.of(
                new Server().url(url)
        )).info(
                info
        );
    }
}
