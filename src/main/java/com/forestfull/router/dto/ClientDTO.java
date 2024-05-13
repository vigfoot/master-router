package com.forestfull.router.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClientDTO {
    private Long id;
    private String code;
    private String token;
    private String description;

    @Data
    public static class History {
        private String type;
        private String ip_address;
        private String data;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime created_time;
    }
}