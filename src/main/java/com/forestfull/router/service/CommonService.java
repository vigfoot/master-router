package com.forestfull.router.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.forestfull.router.dto.RequestHistoryDTO;
import com.forestfull.router.repository.RequestHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommonService {

    private final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    private final RequestHistoryRepository requestHistoryRepository;

    public Mono<Void> recordRequestHistory(ServerHttpRequest request) {
        if (request.getURI().getPath().contains("favicon.ico")) return Mono.empty();

        final ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();
        try {
            return requestHistoryRepository.save(RequestHistoryDTO.builder()
                    .uri(writer.writeValueAsString(request.getURI()))
                    .request_header(writer.writeValueAsString(request.getHeaders()))
                    .build()).then();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Mono<ServerHttpRequest> setIpAddressToRequestHeader(ServerHttpRequest request) {
        final HttpHeaders headers = HttpHeaders.writableHttpHeaders(request.getHeaders()); // header none read only 변경

        Arrays.stream(IP_HEADER_CANDIDATES).map(headers::get)
                .filter(ipFromHeader -> !ObjectUtils.isEmpty(ipFromHeader)).map(ipFromHeader -> ipFromHeader.stream()
                        .filter(StringUtils::hasText)
                        .collect(Collectors.joining(", ")))
                .findFirst()
                .ifPresent(ipAddressString -> headers.add("ipAddress", ipAddressString));

        if (ObjectUtils.isEmpty(headers.get("ipAddress"))) {
            final InetSocketAddress remoteAddress = request.getRemoteAddress();

            if (Objects.isNull(remoteAddress))
                throw new RuntimeException(HttpStatus.BAD_REQUEST.name());

            headers.set("ipAddress", remoteAddress.toString());
        }
        return Mono.just(request);
    }
}