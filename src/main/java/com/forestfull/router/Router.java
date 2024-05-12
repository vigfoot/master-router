package com.forestfull.router;

import com.forestfull.router.dto.NetworkVO;
import com.forestfull.router.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
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
    Mono<ResponseEntity<NetworkVO.Response<String>>> solutionSupport(@PathVariable String solution, @RequestBody NetworkVO.Request request) {
        if (ObjectUtils.isEmpty(request))
            throw new RuntimeException(HttpStatus.BAD_REQUEST.name());

        return supportService.solutionSupport(solution, request)
                .map(isProcessed -> isProcessed
                        ? ResponseEntity
                        .ok(new NetworkVO.Response<>(NetworkVO.DATA_TYPE.STRING, "completed"))
                        : ResponseEntity
                        .status(HttpStatus.NOT_MODIFIED.value())
                        .body(new NetworkVO.Response<>(NetworkVO.DATA_TYPE.ERROR, "잠시후 다시 시도")));
    }
}