package com.forestfull.router.config;

import com.forestfull.router.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class ExceptionResponse {

    @ExceptionHandler({Exception.class})
    ResponseEntity<ResponseDTO<String>> isError(Exception e) {
        if (Objects.equals(HttpStatus.BAD_REQUEST.name(), e.getMessage()))
            return ResponseEntity
                    .badRequest()
                    .body(new ResponseDTO<>(ResponseDTO.DATA_TYPE.ERROR, HttpStatus.BAD_REQUEST.getReasonPhrase()));

        return ResponseEntity.internalServerError()
                .body(new ResponseDTO<>(ResponseDTO.DATA_TYPE.ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()));
    }
}