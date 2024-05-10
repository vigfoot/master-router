package com.forestfull.router.dto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ResponseDTO<T> {

    public final DATA_TYPE dataType;
    public final T contents;

    public enum DATA_TYPE {
        STRING, JSON, XML, JS_SCRIPT, ERROR
    }
}