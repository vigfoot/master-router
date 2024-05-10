package com.forestfull.router.service;

import com.forestfull.router.dto.NetworkVO;
import com.forestfull.router.dto.TokenDTO;
import com.forestfull.router.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CallService {

    private final SchedulerManager schedulerManager;
    private final TokenRepository supportRepository;

    public String getSolution(String token) {
        if (ObjectUtils.isEmpty(SchedulerManager.tokenSet))
            schedulerManager.setTokenSet();

        final String solution = SchedulerManager.tokenSet.get(token);
        return StringUtils.hasText(solution) ? solution : null;
    }

    public Mono<NetworkVO.Response<TokenDTO>> getSupportComponent() {
        return supportRepository.getTokenList()
                .collectList()
                .map(list -> {
                    final TokenDTO first = list.getFirst();
                    return new NetworkVO.Response<>(NetworkVO.DATA_TYPE.STRING, first);
                });
    }
}