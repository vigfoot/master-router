package com.forestfull.router.dto;

import lombok.Data;

@Data
public class TokenDTO {

    private Long client_id;
    private String solution;
    private String token;
}