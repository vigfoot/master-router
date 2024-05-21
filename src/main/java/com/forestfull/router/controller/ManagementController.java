package com.forestfull.router.controller;

import com.forestfull.router.entity.NetworkVO;
import com.forestfull.router.service.ClientService;
import com.forestfull.router.service.ManagementService;
import com.forestfull.router.service.SupportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ManagementController {

    private final ClientService clientService;
    private final SupportService supportService;
    private final ManagementService managementService;

    public static class URI {
        public static final String management = "/management";
    }



    @GetMapping(URI.management)
    NetworkVO.Response<String> test() {
        return NetworkVO.Response.ok(NetworkVO.DATA_TYPE.STRING, "Hello world");
    }
}
