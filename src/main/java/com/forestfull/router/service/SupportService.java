package com.forestfull.router.service;

import com.forestfull.router.dto.ClientDTO;
import com.forestfull.router.dto.NetworkVO;
import com.forestfull.router.repository.ClientHistoryRepository;
import com.forestfull.router.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final SchedulerManager schedulerManager;
    private final ClientRepository clientRepository;
    private final ClientHistoryRepository clientHistoryRepository;

    public String getSupportComponent() {
        if (ObjectUtils.isEmpty(SchedulerManager.componentMap))
            schedulerManager.setComponentMap();

        final String solution = SchedulerManager.componentMap.get("management").getContents();
        return StringUtils.hasText(solution) ? solution : null;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<ClientDTO.History> requestForSolutionSupport(String token, String solution, NetworkVO.Request request) {
        return clientRepository.getClientIdByCodeAndToken(token, solution)
                .flatMap(id -> clientHistoryRepository.save(ClientDTO.History.builder()
                        .client_id(id)
                        .type("request")
                        .ip_address("test")
                        .data(request)
                        .build()));
    }
}