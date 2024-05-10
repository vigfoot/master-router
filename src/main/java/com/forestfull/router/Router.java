package com.forestfull.router;

import com.forestfull.router.dto.ComponentDTO;
import com.forestfull.router.dto.TokenDTO;
import com.forestfull.router.service.CommonService;
import com.forestfull.router.dto.NetworkVO;
import com.forestfull.router.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class Router {

    private final SupportService supportService;

    public static class URI {
        public static final String support = "/support";
    }

    @GetMapping(URI.support)
    ResponseEntity<NetworkVO.Response<String>> getSupportComponent() {
        final String supportComponent = supportService.getSupportComponent();
        return StringUtils.hasText(supportComponent)
                ? ResponseEntity.ok(new NetworkVO.Response<>(NetworkVO.DATA_TYPE.STRING, supportComponent))
                : ResponseEntity.noContent().build();
    }

    @PostMapping(URI.support + "/{solution}")
    Mono<ResponseEntity<NetworkVO.Response<TokenDTO>>> solutionSupport(@PathVariable String solution, @RequestBody NetworkVO.Request request) {

        return Mono.empty();
    }
}