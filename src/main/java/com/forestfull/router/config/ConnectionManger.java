package com.forestfull.router.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.forestfull.router.Router;
import com.forestfull.router.service.ClientService;
import com.forestfull.router.service.CommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
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

    private final CommonService commonService;
    private final ClientService clientService;

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
        final String[] uriPatterns = Arrays.stream(Router.URI.class.getFields())
                .map(field -> {
                    try {
                        return field.get(Router.URI.class.getFields());
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
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .anonymous(ServerHttpSecurity.AnonymousSpec::disable)
                .addFilterAfter((exchange, chain) -> {
                    commonService.setIpAddressToRequestHeader(exchange.getRequest());
                    commonService.recordRequestHistory(exchange.getRequest());
                    return chain.filter(exchange);
                }, SecurityWebFiltersOrder.REACTOR_CONTEXT)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(spec -> spec.pathMatchers(HttpMethod.GET, uriPatterns)
                        .access((authentication, context) -> {
                            final String token = context.getExchange().getRequest().getQueryParams().get("token").getFirst();
                            return Mono.just(new AuthorizationDecision(StringUtils.hasText(clientService.getSolution(token))));
                        }))
                .authorizeExchange(spec -> spec.pathMatchers(HttpMethod.POST, uriPatterns)
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
                        }))
                .build();
    }

    @Bean
    WebClient webClient() {
        return WebClient.builder().build();
    }
}