package com.forestfull.router.service;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CallService {

    private final DatabaseClient DatabaseClient;
    private static Set<String> tokenSet;


    public boolean isCorrectedToken(String token) {
        if (ObjectUtils.isEmpty(tokenSet)) setTokenSet();

        return tokenSet.contains(token) || "hi".equals(token);
    }

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    void setTokenSet() {
        tokenSet = DatabaseClient.sql("SHOW TABLES")
                .fetch()
                .all()
                .map(map -> Arrays.stream(map.values().toArray()).map(String::valueOf).collect(Collectors.joining(",")))
                .collect(Collectors.toSet())
                .block();
    }
}
