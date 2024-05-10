package com.forestfull.router;

import com.forestfull.router.dto.TokenDTO;
import com.forestfull.router.service.CallService;
import com.forestfull.router.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class Router {

    private final WebClient webClient;
    private final CallService callService;

    public static class URI {
        public static final String support = "/support";
    }

    @GetMapping(URI.support)
    Mono<ResponseEntity<ResponseDTO<TokenDTO>>> supportComponent() {
        final Mono<ResponseDTO<TokenDTO>> supportComponent = callService.getSupportComponent();
        return supportComponent.map(ResponseEntity::ok);
    }

    @PostMapping(URI.support + "/{solution}")
    Mono<ResponseEntity<ResponseDTO<TokenDTO>>> requestSupport(@PathVariable String solution) {

        return Mono.empty();
    }
}