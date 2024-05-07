package com.forestfull.router.config;

import com.forestfull.router.GetRouter;
import com.forestfull.router.service.CallService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.PathContainer;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class ConnectionManger {

    private final CallService callService;

    @Bean
    SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authorizeExchange(spec -> spec.pathMatchers(HttpMethod.GET
                                , Arrays.stream(GetRouter.URI.class.getFields())
                                        .map(field -> {
                                            try {
                                                return field.get(GetRouter.URI.class.getFields());
                                            } catch (IllegalAccessException e) {
                                                e.printStackTrace(System.out);
                                                log.error(e.getMessage());
                                                return null;
                                            }
                                        })
                                        .map(uri -> uri + "/**")
                                        .toArray(String[]::new))
                        .access((authentication, context) -> {
                            final String token = context.getExchange()
                                    .getRequest()
                                    .getPath()
                                    .pathWithinApplication()
                                    .elements().getLast()
                                    .value();

                            final boolean correctedToken = callService.isCorrectedToken(token);

                            return Mono.just(new AuthorizationDecision(correctedToken));
                        }))
                .build();
    }

    @Bean
    WebClient webClient() {
        return WebClient.builder().build();
    }
}