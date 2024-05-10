package com.forestfull.router.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CommonService {

    private final SchedulerManager schedulerManager;

    public String getSolution(String token) {
        if (ObjectUtils.isEmpty(SchedulerManager.tokenMap))
            schedulerManager.setTokenMap();

        final String solution = SchedulerManager.tokenMap.get(token);
        return StringUtils.hasText(solution) ? solution : null;
    }
}