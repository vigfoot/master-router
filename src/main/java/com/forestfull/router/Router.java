package com.forestfull.router;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.http.HttpResponse;

@RestController
@RequiredArgsConstructor
public class Router {

    private final WebClient webClient;

}
