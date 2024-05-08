package com.forestfull.router.service;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CallService {

    private final DatabaseClient DatabaseClient;
    private static Map<String, String> tokenSet;

    public String getSolution(String token) {
        if (ObjectUtils.isEmpty(tokenSet)) setTokenSet();

        final String solution = tokenSet.get(token);
        return StringUtils.hasText(solution) ? solution : null;
    }

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    void setTokenSet() {
        tokenSet = DatabaseClient.sql("SELECT t.solution, t.token FROM token t WHERE t.is_used")
                .fetch()
                .all()
                .collect(Collectors.toMap(map -> String.valueOf(map.get("token")), map -> String.valueOf(map.get("solution"))))
                .block();
    }
}