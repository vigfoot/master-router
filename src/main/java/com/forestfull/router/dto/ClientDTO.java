package com.forestfull.router.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("client")
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
        private NetworkVO.Request data;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime created_time;
    }
}