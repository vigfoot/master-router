package com.forestfull.router.config;

import com.forestfull.router.controller.ClientController;
import com.forestfull.router.controller.ManagementController;
import com.forestfull.router.service.ClientService;
import com.forestfull.router.service.CommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class ConnectionManger {

    @Value("${db.user}")
    private String userName;
    @Value("${db.password}")
    private String userPassword;

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final CommonService commonService;
    private final ClientService clientService;


    @Bean
    MapReactiveUserDetailsService userDetailsService() {
        return new MapReactiveUserDetailsService(User.builder()
                .username(userName)
                .password(passwordEncoder().encode(userPassword))
                .roles("MANAGER")
                .build());
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        final String[] clientUriPatterns = Arrays.stream(ClientController.URI.class.getFields())
                .map(field -> {
                    try {
                        return field.get(ClientController.URI.class.getFields());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace(System.out);
                        log.error(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(uri -> uri + "/**")
                .toArray(String[]::new);

        final String[] managementUriPatterns = Arrays.stream(ManagementController.URI.class.getFields())
                .map(field -> {
                    try {
                        return field.get(ManagementController.URI.class.getFields());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace(System.out);
                        log.error(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(uri -> uri + "/**")
                .toArray(String[]::new);

        return http
                .httpBasic(Customizer.withDefaults())
                .formLogin(spec -> spec.authenticationSuccessHandler((webFilterExchange, authentication) -> {
                    authentication.setAuthenticated(true);
                    final ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
                    response.setStatusCode(HttpStatus.OK);
                    return response.setComplete();
                }))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(spec -> spec.pathMatchers(HttpMethod.GET, clientUriPatterns)
                        .access((authentication, context) -> {
                            final List<String> tokenList = context.getExchange().getRequest().getQueryParams().get("token");
                            return Mono.just(new AuthorizationDecision(!ObjectUtils.isEmpty(tokenList) && StringUtils.hasText(clientService.getSolution(tokenList.getFirst()))));
                        })
                )
                .authorizeExchange(spec -> spec.pathMatchers(HttpMethod.POST, clientUriPatterns)
                        .access((authentication, context) -> {
                            final ServerHttpRequest request = context.getExchange().getRequest();
                            final String token = request.getHeaders().getFirst("token");
                            final List<String> pathList = Arrays.stream(request.getPath()
                                            .pathWithinApplication().value()
                                            .split("/"))
                                    .filter(StringUtils::hasText)
                                    .toList();

                            if (ObjectUtils.isEmpty(pathList)) return Mono.empty();

                            final String solution = pathList.getLast();

                            return Mono.just(new AuthorizationDecision(solution.equalsIgnoreCase(clientService.getSolution(token))));
                        })
                )
                .authorizeExchange(spec -> spec.pathMatchers(managementUriPatterns).hasRole("MANAGER"))
                .addFilterAfter((exchange, chain) -> {
                    if (exchange.getRequest().getURI().getPath().contains("login")) return chain.filter(exchange);
                    if (Arrays.stream(clientUriPatterns)
                            .noneMatch(pattern -> pathMatcher.match(pattern, exchange.getRequest().getURI().getPath())))
                        return chain.filter(exchange);

                    return chain.filter(exchange)
                            .then(Mono.defer(() -> {
                                commonService.setIpAddressToRequestHeader(exchange.getRequest());
                                return commonService.recordRequestHistory(exchange.getRequest());
                            }));
                }, SecurityWebFiltersOrder.LAST)
                .build();
    }
}