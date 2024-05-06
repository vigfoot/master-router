package com.forestfull.router.service;

import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

public class SchedulerManager {


    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    void getToken() {
        
    }


}
