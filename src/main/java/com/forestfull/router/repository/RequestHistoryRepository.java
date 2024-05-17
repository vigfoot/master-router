package com.forestfull.router.repository;

import com.forestfull.router.dto.RequestHistoryDTO;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface RequestHistoryRepository extends ReactiveCrudRepository<RequestHistoryDTO, Long> {



}