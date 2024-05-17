package com.forestfull.router.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forestfull.router.dto.ClientDTO;
import com.forestfull.router.entity.NetworkVO;
import com.forestfull.router.service.ClientService;
import com.forestfull.router.service.SupportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final SupportService supportService;

    public static class URI {
        public static final String dataType = "/data-type";
        public static final String support = "/support";
    }

    @GetMapping(URI.dataType)
    NetworkVO.Response<String> getDataType() {
        log.info(URI.dataType);
        try {
            return NetworkVO.Response.ok(NetworkVO.DATA_TYPE.JSON, new ObjectMapper().writerWithDefaultPrettyPrinter()
                    .writeValueAsString(Arrays.stream(NetworkVO.DATA_TYPE.values())
                            .map(Enum::name)
                            .collect(Collectors.toList())));

        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
    }

    @GetMapping(URI.support)
    NetworkVO.Response<String> getSupportComponent() {
        log.info(URI.support);
        final String supportComponent = supportService.getSupportComponent();

        return StringUtils.hasText(supportComponent)
                ? NetworkVO.Response.ok(NetworkVO.DATA_TYPE.JS_SCRIPT, supportComponent)
                : NetworkVO.Response.fail(HttpStatus.NOT_FOUND);
    }

    @GetMapping(URI.support + "/history")
    Flux<ClientDTO.History> getSupportHistory(String token) {
        log.info(URI.support + "/history");
        return clientService.getSupportHistory(token);
    }

    @PostMapping(URI.support + "/{solution}")
    Mono<NetworkVO.Response<?>> requestForSolutionSupport(
            @RequestHeader String token
            , @RequestHeader String ipAddress
            , @PathVariable String solution
            , @RequestBody NetworkVO.Request request) {
        log.info(URI.support + "/" + solution + " + " + token);
        if (ObjectUtils.isEmpty(request))
            throw new RuntimeException(HttpStatus.BAD_REQUEST.name());

        return supportService.requestForSolutionSupport(token, solution, ipAddress, request)
                .then(Mono.fromCallable(() -> NetworkVO.Response.ok(NetworkVO.DATA_TYPE.JSON, Collections.singletonMap("success", true))));
    }
}