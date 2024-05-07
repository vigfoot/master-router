package com.forestfull.router.config;

import com.forestfull.router.PostRouter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableWebFluxSecurity
public class ConnectionManger {

    @Bean
    SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(spec -> {
                    final String[] array = Arrays.stream(PostRouter.URI.class.getFields())
                            .map(field -> {
                                try {
                                    return field.get(PostRouter.URI.class.getFields());
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace(System.out);
                                    log.error(e.getMessage());
                                    return null;
                                }
                            })
                            .map(String::valueOf)
                            .toArray(String[]::new);
                    spec.pathMatchers(HttpMethod.POST, array
                            )
                            .authenticated();
                })
                .build();
    }

    @Bean
    WebClient webClient() {
        return WebClient.builder().build();
    }
}