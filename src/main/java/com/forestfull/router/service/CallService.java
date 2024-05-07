package com.forestfull.router.service;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CallService {

    private final DatabaseClient DatabaseClient;
    private static Set<String> tokenSet;


    public boolean isCorrectedToken(String token) {
        if (ObjectUtils.isEmpty(tokenSet)) {
            DatabaseClient.sql("SHOW TABLES")
                    .fetch()
                    .all()
                    .subscribe(map -> System.out.println(map.toString()));
        }

        return tokenSet.contains(token);
    }
}
