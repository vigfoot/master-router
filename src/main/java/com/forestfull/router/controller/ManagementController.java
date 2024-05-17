package com.forestfull.router.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ManagementController {

    public static class URI {
        public static final String management = "/management";
    }

    @PostMapping(URI.management)
    Mono<String> test() {
        return Mono.empty();
    }
}
