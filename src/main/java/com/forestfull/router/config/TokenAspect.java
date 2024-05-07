package com.forestfull.router.config;

import com.forestfull.router.service.CallService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;


@Aspect
@Component
@RequiredArgsConstructor
public class TokenAspect {

    private final CallService callService;

    @Before("execution(* com.forestfull.router.GetRouter.*(..))")
    void isCorrectedToken(JoinPoint joinPoint){
        if (ObjectUtils.isEmpty(joinPoint.getArgs())) return;

//        joinPoint.getArgs()


        throw new RuntimeException(HttpStatus.BAD_REQUEST.name());
    }

}