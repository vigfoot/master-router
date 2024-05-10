package com.forestfull.router.service;

import com.forestfull.router.dto.TokenDTO;
import com.forestfull.router.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SchedulerManager {

    public static Map<String, String> tokenSet;

    private final TokenRepository tokenRepository;

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    void setTokenSet() {
        tokenSet = tokenRepository.getTokenList()
                .collect(Collectors.toMap(TokenDTO::getToken, TokenDTO::getSolution))
                .block();

    }
}