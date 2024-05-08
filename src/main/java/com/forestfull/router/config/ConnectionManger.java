package com.forestfull.router.config;

import com.forestfull.router.GetRouter;
import com.forestfull.router.service.CallService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.PathContainer;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .anonymous(ServerHttpSecurity.AnonymousSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
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
                                        .filter(Objects::nonNull)
                                        .map(uri -> uri + "/**")
                                        .toArray(String[]::new))
                        .access((authentication, context) -> {
                            final List<String> pathList = Arrays.stream(context.getExchange()
                                    .getRequest()
                                    .getPath()
                                    .pathWithinApplication().value()
                                    .split("/"))
                                    .filter(StringUtils::hasText)
                                    .toList();

                            if (ObjectUtils.isEmpty(pathList) || pathList.size() < 3) return Mono.empty();

                            final String solution = pathList.get(pathList.size() - 2);
                            final String token = pathList.getLast();

                            return Mono.just(new AuthorizationDecision(solution.equalsIgnoreCase(callService.getSolution(token))));
                        }))
                .build();
    }

    @Bean
    WebClient webClient() {
        return WebClient.builder().build();
    }
}