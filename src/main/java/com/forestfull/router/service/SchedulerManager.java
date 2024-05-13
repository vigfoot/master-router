package com.forestfull.router.service;

import com.forestfull.router.dto.ComponentDTO;
import com.forestfull.router.dto.ClientDTO;
import com.forestfull.router.repository.ComponentRepository;
import com.forestfull.router.repository.ClientRepository;
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

    private final ClientRepository clientRepository;
    private final ComponentRepository componentRepository;

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    void setTokenMap() {
        tokenMap = clientRepository.getTokenList()
                .collect(Collectors.toMap(ClientDTO::getToken, ClientDTO::getCode))
                .block();
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    void setComponentMap() {
        componentMap = componentRepository.getSupportComponent()
                .filter(dto -> ObjectUtils.isEmpty(componentMap)
                        || componentMap.values().stream()
                        .filter(c -> Objects.equals(dto.getMethod_name(), c.getMethod_name()))
                        .noneMatch(c -> dto.getCreated_time().isEqual(c.getCreated_time())))
                .collect(Collectors.toMap(ComponentDTO::getMethod_name, componentDto -> componentDto))
                .block();
    }
}