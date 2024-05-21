package com.forestfull.router.config;

import com.forestfull.router.controller.ClientController;
import com.forestfull.router.controller.ManagementController;
import com.forestfull.router.entity.NetworkVO;
import com.forestfull.router.service.ClientService;
import com.forestfull.router.service.CommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class ConnectionManger {

    private static final String ROLE_MANAGER = "MANAGER";
    @Value("${db.user}")
    private String userName;
    @Value("${db.password}")
    private String userPassword;

    private final CommonService commonService;
    private final ClientService clientService;

    @Bean
    MapReactiveUserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        final UserDetails user = User.builder()
                .username(userName)
                .password(userPassword)
                .passwordEncoder(passwordEncoder::encode)
                .roles(ROLE_MANAGER)
                .build();

        return new MapReactiveUserDetailsService(Collections.singletonMap(userName, user));
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        final String[] resourcesUriPatterns = {"/favicon.ico, /util"};
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
                .headers(spec -> spec.writer(exchange -> {
                    if (exchange.getRequest().getURI().getPath().contains("login")) return Mono.empty();
                    if (Arrays.stream(clientUriPatterns)
                            .noneMatch(pattern -> new AntPathMatcher().match(pattern, exchange.getRequest().getURI().getPath())))
                        return Mono.empty();

                    return commonService.setIpAddressToRequestHeader(exchange.getRequest())
                            .flatMap(commonService::recordRequestHistory);
                }))
                .httpBasic(Customizer.withDefaults())
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
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
                .authorizeExchange(spec -> spec.pathMatchers(managementUriPatterns).hasRole(ROLE_MANAGER))
                .exceptionHandling(spec -> spec.accessDeniedHandler((exchange, denied) -> {
                    final NetworkVO.Response<String> errorResponse = ExceptionResponse.getErrorResponse(denied);
                    exchange.getResponse().setStatusCode(errorResponse.getStatusCode());

                    assert errorResponse.getBody() != null;
                    final byte[] bytes = errorResponse.getBody().getBytes(StandardCharsets.UTF_8);
                    final DataBuffer responseDataBuffer = exchange.getResponse().bufferFactory().wrap(bytes);

                    return exchange.getResponse().writeWith(Mono.just(responseDataBuffer));
                }))
                .build();
    }
}