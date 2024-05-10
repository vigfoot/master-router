package com.forestfull.router.repository;

import com.forestfull.router.dto.ComponentDTO;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SupportRepository extends ReactiveCrudRepository<ComponentDTO, Long> {

    @Query("SELECT c.contents FROM component c WHERE c.method_name = 'management'")
    Mono<String> getSupportComponent();
}
