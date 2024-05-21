package com.forestfull.router.config;

import com.forestfull.router.entity.NetworkVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class ExceptionResponse {

    @ExceptionHandler({Exception.class})
    public static NetworkVO.Response<String> isError(Exception e) {
        e.printStackTrace(System.out);

        if ("Access Denied".equalsIgnoreCase(e.getMessage()))
            return NetworkVO.Response.fail(HttpStatus.UNAUTHORIZED, NetworkVO.DATA_TYPE.ERROR.name());

        if (HttpStatus.UNAUTHORIZED.name().equalsIgnoreCase(e.getMessage()))
            return NetworkVO.Response.fail(HttpStatus.UNAUTHORIZED, NetworkVO.DATA_TYPE.ERROR.name());

        if (HttpStatus.BAD_REQUEST.name().equalsIgnoreCase(e.getMessage()))
            return NetworkVO.Response.fail(HttpStatus.BAD_REQUEST, NetworkVO.DATA_TYPE.ERROR.name());

        return NetworkVO.Response.fail(HttpStatus.INTERNAL_SERVER_ERROR, NetworkVO.DATA_TYPE.ERROR.name());
    }
}