package com.forestfull.router;

import com.forestfull.router.service.CallService;
import com.forestfull.router.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequiredArgsConstructor
public class Router {

    private final WebClient webClient;
    private final CallService callService;

    public static class URI {
        public static final String support = "/support";
    }

    @GetMapping(URI.support)
    ResponseEntity<ResponseDTO> supportComponent() {
        return ResponseEntity.ok(callService.supportComponent());
    }

    @PostMapping(URI.support + "/{solution}")
    ResponseEntity<ResponseDTO> requestSupport(@PathVariable String solution) {

        return ResponseEntity.ok(ResponseDTO.builder().build());
    }
}