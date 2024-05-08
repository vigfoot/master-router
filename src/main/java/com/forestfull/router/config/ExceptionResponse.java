package com.forestfull.router.config;

import com.forestfull.router.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class ExceptionResponse {

    @ExceptionHandler({RuntimeException.class, Exception.class})
    ResponseEntity<ResponseDTO> isError(Exception e) {
        if (Objects.equals(HttpStatus.BAD_REQUEST.name(), e.getMessage()))
            return ResponseEntity
                    .badRequest()
                    .body(ResponseDTO.builder()
                            .dataType(ResponseDTO.DATA_TYPE.ERROR)
                            .contents(HttpStatus.BAD_REQUEST.getReasonPhrase())
                            .build());

        return ResponseEntity.internalServerError()
                .body(ResponseDTO.builder()
                        .dataType(ResponseDTO.DATA_TYPE.ERROR)
                        .contents(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .build());
    }
}