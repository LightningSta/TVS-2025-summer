package org.example.formulaserivce.Configurations;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
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
        String url = "http://localhost:"+port;
        Info info = new Info();
        info.setTitle("Formula Service");
        info.setDescription("Сервис для работы с формулами");
        return new OpenAPI().servers(List.of(
                new Server().url(url)
        ))
                .info(
                      info
                );
    }
}
