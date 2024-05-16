package com.forestfull.router.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ClientDTO {
    private Long id;
    private String code;
    private String token;
    private String description;

    @Data
    @Builder
    public static class History {
        private Long client_id;
        private String type;
        private String ip_address;
        private String data;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime created_time;

    }
}