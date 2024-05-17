package com.forestfull.router.service;

import com.forestfull.router.dto.ClientDTO;
import com.forestfull.router.repository.ClientHistoryRepository;
import com.forestfull.router.repository.ClientRepository;
import com.forestfull.router.util.SchedulerManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientHistoryRepository clientHistoryRepository;
    private final SchedulerManager schedulerManager;

    public String getSolution(String token) {
        if (ObjectUtils.isEmpty(SchedulerManager.tokenMap))
            schedulerManager.setTokenMap();

        final String solution = SchedulerManager.tokenMap.get(token);
        return StringUtils.hasText(solution) ? solution : null;
    }

    public Flux<ClientDTO.History> getSupportHistory(String token) {
        return clientHistoryRepository.getHistoriesByClient_token(token)
                .collectList()
                .flatMapMany(list -> Flux.just(list.stream().collect(Collectors.groupingBy(ClientDTO.History::getClient_id)))
                        .flatMap(map -> clientRepository.findAllById(map.keySet())
                                .flatMap(c -> Flux.fromIterable(map.get(c.getId()))
                                        .flatMap(ch -> {
                                            ch.setClient(c);
                                            return Flux.just(ch);
                                        }))
                        ));
    }
}