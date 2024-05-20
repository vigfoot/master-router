package com.forestfull.router.config;

import com.forestfull.router.entity.NetworkVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class ExceptionResponse {

    @ExceptionHandler({Exception.class})
    NetworkVO.Response<String> isError(Exception e) {
        e.printStackTrace(System.out);

        if (Objects.equals(HttpStatus.BAD_REQUEST.name(), e.getMessage()))
            return NetworkVO.Response.fail(HttpStatus.BAD_REQUEST, NetworkVO.DATA_TYPE.ERROR.name());

        return NetworkVO.Response.fail(HttpStatus.INTERNAL_SERVER_ERROR, NetworkVO.DATA_TYPE.ERROR.name());
    }
}