package com.forestfull.router.config;

import com.forestfull.router.dto.NetworkVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class ExceptionResponse {

    @ExceptionHandler({Exception.class})
    ResponseEntity<NetworkVO.Response<String>> isError(Exception e) {
        if (Objects.equals(HttpStatus.BAD_REQUEST.name(), e.getMessage()))
            return ResponseEntity.badRequest()
                    .body(new NetworkVO.Response<>(NetworkVO.DATA_TYPE.ERROR, HttpStatus.BAD_REQUEST.getReasonPhrase()));

        return ResponseEntity.internalServerError()
                .body(new NetworkVO.Response<>(NetworkVO.DATA_TYPE.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
    }
}