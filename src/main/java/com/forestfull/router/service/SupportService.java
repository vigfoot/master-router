package com.forestfull.router.service;

import com.forestfull.router.dto.ClientDTO;
import com.forestfull.router.dto.NetworkVO;
import com.forestfull.router.repository.SupportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final SchedulerManager schedulerManager;
    private final SupportRepository supportRepository;

    public String getSupportComponent() {
        if (ObjectUtils.isEmpty(SchedulerManager.componentMap))
            schedulerManager.setComponentMap();

        final String solution = SchedulerManager.componentMap.get("management").getContents();
        return StringUtils.hasText(solution) ? solution : null;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<Boolean> solutionSupport(String solution, NetworkVO.Request request) {
        return Mono.empty();
    }
}