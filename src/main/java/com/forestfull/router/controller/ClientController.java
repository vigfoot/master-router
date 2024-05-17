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
    ResponseEntity<String> getDataType() {
        log.info(URI.dataType);
        try {
            return ResponseEntity.ok(new ObjectMapper().writerWithDefaultPrettyPrinter()
                    .writeValueAsString(Arrays.stream(NetworkVO.DATA_TYPE.values())
                            .map(Enum::name)
                            .collect(Collectors.toList())));

        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
    }

    @GetMapping(URI.support)
    ResponseEntity<NetworkVO.Response<String>> getSupportComponent() {
        log.info(URI.support);
        final String supportComponent = supportService.getSupportComponent();
        return StringUtils.hasText(supportComponent)
                ? ResponseEntity.ok(new NetworkVO.Response<>(NetworkVO.DATA_TYPE.JS_SCRIPT, supportComponent))
                : ResponseEntity.noContent().build();
    }

    @GetMapping(URI.support + "/history")
    ResponseEntity<Flux<ClientDTO.History>> getSupportHistory(String token) {
        log.info(URI.support + "/history");
        return ResponseEntity.ok(clientService.getSupportHistory(token));
    }

    @PostMapping(URI.support + "/{solution}")
    Mono<ResponseEntity<NetworkVO.Response<String>>> requestForSolutionSupport(
            @RequestHeader String token
            , @RequestHeader String ipAddress
            , @PathVariable String solution
            , @RequestBody NetworkVO.Request request) {
        log.info(URI.support + "/" + solution + " + " + token);
        if (ObjectUtils.isEmpty(request))
            throw new RuntimeException(HttpStatus.BAD_REQUEST.name());

        return supportService.requestForSolutionSupport(token, solution, ipAddress, request)
                .then(Mono.fromCallable(() -> ResponseEntity.ok()
                        .body(new NetworkVO.Response<>(NetworkVO.DATA_TYPE.STRING, "Success"))));
    }
}