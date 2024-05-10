package com.forestfull.router.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ComponentDTO {
    private String method_name;
    private String contents;
    private LocalDateTime created_at;
}