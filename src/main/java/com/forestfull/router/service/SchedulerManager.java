package com.forestfull.router.service;

import com.forestfull.router.dto.ComponentDTO;
import com.forestfull.router.dto.TokenDTO;
import com.forestfull.router.repository.SupportRepository;
import com.forestfull.router.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SchedulerManager {

    public static Map<String, String> tokenMap;
    public static Map<String, ComponentDTO> componentMap;

    private final TokenRepository tokenRepository;
    private final SupportRepository supportRepository;

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    void setTokenMap() {
        tokenMap = tokenRepository.getTokenList()
                .collect(Collectors.toMap(TokenDTO::getToken, TokenDTO::getSolution))
                .block();
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    void setComponentMap() {
        componentMap = supportRepository.getSupportComponent()
                .filter(dto -> ObjectUtils.isEmpty(componentMap)
                        || componentMap.values().stream()
                        .filter(c -> Objects.equals(dto.getMethod_name(), c.getMethod_name()))
                        .noneMatch(c -> dto.getCreated_at().isEqual(c.getCreated_at())))
                .collect(Collectors.toMap(ComponentDTO::getMethod_name, componentDto -> componentDto))
                .block();
    }
}