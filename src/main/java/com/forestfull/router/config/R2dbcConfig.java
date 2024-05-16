package com.forestfull.router.config;

import io.r2dbc.proxy.ProxyConnectionFactory;
import io.r2dbc.proxy.core.QueryInfo;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableTransactionManagement
public class R2dbcConfig extends AbstractR2dbcConfiguration {


    @Value("${db.url}")
    private String connectionUrl;

    @Bean
    @NonNull
    @Override
    public ConnectionFactory connectionFactory() {
        return ProxyConnectionFactory.builder(ConnectionFactories.get(connectionUrl))
                .onAfterQuery(queryExecutionInfo -> {
                    for (QueryInfo query : queryExecutionInfo.getQueries()) {
                        final String bindArguments = query.getBindingsList().stream()
                                .map(q -> q.getIndexBindings().stream()
                                        .map(qq -> "'" + qq.getBoundValue().getValue() + "'")
                                        .collect(Collectors.joining(", ")))
                                .map(String::valueOf)
                                .collect(Collectors.joining(", "));
                        if (StringUtils.hasText(bindArguments)) {
                            log.info("[completed] {} (bindings: {})", query.getQuery(), bindArguments);
                        } else {
                            log.info("[completed] {}", query.getQuery());
                        }
                    }
                })
                .build();
    }
}