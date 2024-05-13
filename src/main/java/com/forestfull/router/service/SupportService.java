package com.forestfull.router.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forestfull.router.dto.ClientDTO;
import com.forestfull.router.dto.NetworkVO;
import com.forestfull.router.repository.ClientHistoryRepository;
import com.forestfull.router.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final SchedulerManager schedulerManager;
    private final ClientHistoryRepository clientHistoryRepository;

    public String getSupportComponent() {
        if (ObjectUtils.isEmpty(SchedulerManager.componentMap))
            schedulerManager.setComponentMap();

        final String solution = SchedulerManager.componentMap.get("management").getContents();
        return StringUtils.hasText(solution) ? solution : null;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<Boolean> requestForSolutionSupport(String token, String solution, NetworkVO.Request request) {
        try {
            return clientHistoryRepository.saveHistoryByTokenAndSolution(token, solution,"0.0.0.01", new ObjectMapper().writeValueAsString(request));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}