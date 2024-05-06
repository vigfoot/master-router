package com.forestfull.router.config;

import com.forestfull.router.PostRouter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableWebFluxSecurity
public class ConnectionManger {

    @Bean
    SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(spec -> {
                    spec.pathMatchers(HttpMethod.POST, PostRouter.URI.support)
                            .authenticated();
                })
                .build();

    }

    @Bean
    WebClient webClient() {
        return WebClient.builder().build();
    }
}