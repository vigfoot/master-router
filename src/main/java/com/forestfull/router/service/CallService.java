package com.forestfull.router.service;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
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

        return tokenSet.contains(token);
    }

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    void setTokenSet() {
        tokenSet = DatabaseClient.sql("SELECT t.token FROM token t WHERE t.is_used")
                .fetch()
                .all()
                .map(map -> String.valueOf(map.get("token")))
                .collect(Collectors.toSet())
                .block();
    }
}