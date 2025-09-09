package org.example.apigateway.Filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Component
public class AuthFilterFactory extends AbstractGatewayFilterFactory<AuthFilterFactory.Config> {

    public AuthFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        boolean b=true;
        return (exchange, chain) -> {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            System.out.println("validate "+exchange.getRequest().getURI());
            if(exchange.getRequest().getURI().getPath().endsWith("/api/formulas/create")||
                    exchange.getRequest().getURI().getPath().endsWith("/api/formulas/import")||
            exchange.getRequest().getURI().getPath().contains("swagger")){
                return chain.filter(exchange);
            }
            if(exchange.getRequest().getHeaders().containsKey("Authorization")){
                headers.add("Authorization", exchange.getRequest().getHeaders().get("Authorization").get(0));
                HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
                try {
                    ResponseEntity<Void> response = restTemplate.exchange(
                            "http://localhost:8082/api/auth/tokenVal",
                            HttpMethod.POST,
                            httpEntity,
                            Void.class
                    );

                    if (response.getStatusCode().is2xxSuccessful()) {
                        System.out.println("Token is valid: HTTP " + response.getStatusCode());
                        return chain.filter(exchange);
                    } else {
                        System.out.println("Token validation failed: HTTP " + response.getStatusCode());
                        exchange.getResponse().setStatusCode(response.getStatusCode());
                        return exchange.getResponse().setComplete();
                    }
                } catch (HttpClientErrorException ex) {
                    System.out.println("Token validation failed: " + ex.getStatusCode());
                    exchange.getResponse().setStatusCode(ex.getStatusCode());
                    return exchange.getResponse().setComplete();
                } catch (RestClientException ex) {
                    System.out.println("Request failed: " + ex.getMessage());
                    return exchange.getResponse().setComplete();
                }
            }else{
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }

    public static class Config {
    }
}

