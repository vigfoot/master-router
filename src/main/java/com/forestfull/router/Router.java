package com.forestfull.router;

import com.forestfull.router.dto.TokenDTO;
import com.forestfull.router.service.CallService;
import com.forestfull.router.dto.NetworkVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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
    Mono<ResponseEntity<NetworkVO.Response<String>>> getSupportComponent() {
        return callService.getSupportComponent()
                .map(cp -> StringUtils.hasText(cp)
                        ? ResponseEntity.ok(new NetworkVO.Response<>(NetworkVO.DATA_TYPE.STRING, cp))
                        : ResponseEntity.noContent().build());
    }

    @PostMapping(URI.support + "/{solution}")
    Mono<ResponseEntity<NetworkVO.Response<TokenDTO>>> solutionSupport(@PathVariable String solution, @RequestBody NetworkVO.Request request) {

        return Mono.empty();
    }
}