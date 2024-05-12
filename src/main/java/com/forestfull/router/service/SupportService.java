package com.forestfull.router.service;

import com.forestfull.router.dto.NetworkVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final SchedulerManager schedulerManager;

    public String getSupportComponent() {
        if (ObjectUtils.isEmpty(SchedulerManager.componentMap))
            schedulerManager.setComponentMap();

        final String solution = SchedulerManager.componentMap.get("management").getContents();
        return StringUtils.hasText(solution) ? solution : null;
    }

    public Mono<Boolean> solutionSupport(String solution, NetworkVO.Request request) {
        return Mono.just(false);
    }
}