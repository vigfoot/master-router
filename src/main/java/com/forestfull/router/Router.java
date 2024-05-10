package com.forestfull.router;

import com.forestfull.router.dto.TokenDTO;
import com.forestfull.router.service.CallService;
import com.forestfull.router.dto.NetworkVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    Mono<ResponseEntity<NetworkVO.Response<TokenDTO>>> getSupportComponent() {
        final Mono<NetworkVO.Response<TokenDTO>> supportComponent = callService.getSupportComponent();
        return supportComponent.map(ResponseEntity::ok);
    }

    @PostMapping(URI.support + "/{solution}")
    Mono<ResponseEntity<NetworkVO.Response<TokenDTO>>> solutionSupport(@PathVariable String solution, @RequestBody NetworkVO.Request request) {

        return Mono.empty();
    }
}