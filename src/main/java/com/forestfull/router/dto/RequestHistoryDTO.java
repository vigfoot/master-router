package com.forestfull.router.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@Table("request_history")
public class RequestHistoryDTO {
    private Long id;
    private String uri;
    private String request_header;
    private String request_body;
    private LocalDateTime created_time;
}