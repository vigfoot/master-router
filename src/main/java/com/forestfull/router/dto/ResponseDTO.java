package com.forestfull.router.dto;

import lombok.Builder;

@Builder
public class ResponseDTO {

    public DATA_TYPE dataType;
    public Object contents;

    public enum DATA_TYPE {
        STRING, JSON, XML, JS_SCRIPT, ERROR
    }
}