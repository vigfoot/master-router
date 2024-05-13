package com.forestfull.router;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forestfull.router.dto.ClientDTO;
import com.forestfull.router.dto.NetworkVO;
import com.forestfull.router.service.ClientService;
import com.forestfull.router.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class Router {

    private final ClientService clientService;
    private final SupportService supportService;

    public static class URI {
        public static final String dataType = "/data-type";
        public static final String support = "/support";
    }

    @GetMapping(URI.dataType)
    ResponseEntity<String> getDataType() {
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
        final String supportComponent = supportService.getSupportComponent();
        return StringUtils.hasText(supportComponent)
                ? ResponseEntity.ok(new NetworkVO.Response<>(NetworkVO.DATA_TYPE.JS_SCRIPT, supportComponent))
                : ResponseEntity.noContent().build();
    }

    @GetMapping(URI.support + "/history")
    ResponseEntity<Flux<ClientDTO.History>> getSupportHistory(String token) {
        return ResponseEntity.ok(clientService.getSupportHistory(token));
    }

    @PostMapping(URI.support + "/{solution}")
    Mono<ResponseEntity<?>> requestForSolutionSupport(@RequestHeader String token, @PathVariable String solution, @RequestBody NetworkVO.Request request) {
        if (ObjectUtils.isEmpty(request))
            throw new RuntimeException(HttpStatus.BAD_REQUEST.name());

        return supportService.requestForSolutionSupport(token, solution, request)
                .map(client -> Objects.nonNull(client)
                        ? ResponseEntity.ok("completed")
                        : ResponseEntity.status(HttpStatus.NOT_MODIFIED.value()).body(new NetworkVO.Response<>(NetworkVO.DATA_TYPE.ERROR, "잠시후 다시 시도")));
    }
}