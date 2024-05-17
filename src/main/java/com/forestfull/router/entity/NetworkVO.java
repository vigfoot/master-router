package com.forestfull.router.entity;

import lombok.RequiredArgsConstructor;

import java.util.LinkedHashMap;

public class NetworkVO {

    public static class Request extends LinkedHashMap<String, Object> {
    }

    @RequiredArgsConstructor
    public static class Response<T> {
        public final DATA_TYPE dataType;
        public final T contents;
    }

    public enum DATA_TYPE {
        STRING, JSON, XML, JS_SCRIPT, ERROR
    }
}