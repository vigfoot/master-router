package com.forestfull.router.repository;

import com.forestfull.router.dto.TokenDTO;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TokenRepository extends ReactiveCrudRepository<TokenDTO, Long> {


    @Query("SELECT t.solution, t.token FROM token t WHERE t.is_used")
    Flux<TokenDTO> getTokenList();
}
