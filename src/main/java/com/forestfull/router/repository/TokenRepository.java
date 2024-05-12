package com.forestfull.router.repository;

import com.forestfull.router.dto.ClientDTO;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TokenRepository extends ReactiveCrudRepository<ClientDTO, Long> {

    @Query("SELECT c.code, c.token, c.id FROM client c WHERE c.is_used")
    Flux<ClientDTO> getTokenList();
}