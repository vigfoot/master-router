package com.forestfull.router.repository;

import com.forestfull.router.dto.ComponentDTO;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface SupportRepository extends ReactiveCrudRepository<ComponentDTO, Long> {

    @Query("SELECT c.method_name, c.contents, c.created_time FROM component c WHERE c.is_used")
    Flux<ComponentDTO> getSupportComponent();
}
