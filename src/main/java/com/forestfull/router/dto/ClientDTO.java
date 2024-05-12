package com.forestfull.router.dto;

import lombok.Data;

@Data
public class ClientDTO {
    private Long id;
    private String code;
    private String token;
    private String description;
}