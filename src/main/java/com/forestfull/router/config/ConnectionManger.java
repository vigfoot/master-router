package com.forestfull.router.config;

import com.forestfull.router.GetRouter;
import com.forestfull.router.dto.ResponseDTO;
import com.forestfull.router.service.CallService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class ConnectionManger {

    @Value("${db.user}")
    private String userName;
    @Value("${db.password}")
    private String userPassword;

    private final CallService callService;

    @Bean
    MapReactiveUserDetailsService userDetailsService() {
        return new MapReactiveUserDetailsService(User.builder()
                .username(userName)
                .password(userPassword)
                .authorities("manager")
                .build());
    }

    @Bean
    SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
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

                            return Mono.just(new AuthorizationDecision(callService.isCorrectedToken(token)));
                        }))
                .build();
    }

    @Bean
    WebClient webClient() {
        return WebClient.builder().build();
    }
}