package com.forestfull.router.repository;

import com.forestfull.router.dto.ClientDTO;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ClientHistoryRepository extends ReactiveCrudRepository<ClientDTO.History, Long> {
    @Query("SELECT ch.type, ch.ip_address, ch.data FROM client_history ch WHERE ch.client_id = :clientId")
    Flux<ClientDTO.History> getHistoriesByClient_id(Long clientId);

    @Query("SELECT ch.type, ch.ip_address, ch.data, ch.created_time" +
            " FROM client_history ch" +
            " JOIN client c ON ch.client_id = c.id AND c.token = :token")
    Flux<ClientDTO.History> getHistoriesByClient_token(String token);

}