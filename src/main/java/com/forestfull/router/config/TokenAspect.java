package com.forestfull.router.config;

//@Aspect
//@Component
//@RequiredArgsConstructor
public class TokenAspect {

/*
    private final CallService callService;

    @Before("execution(* com.forestfull.router.GetRouter.*(..))")
    void isCorrectedToken(JoinPoint joinPoint){
        if (ObjectUtils.isEmpty(joinPoint.getArgs()))
            throw new RuntimeException(HttpStatus.BAD_REQUEST.name());

        final String token = String.valueOf(joinPoint.getArgs()[joinPoint.getArgs().length - 1]);

        if (!callService.isCorrectedToken(token))
            throw new RuntimeException(HttpStatus.BAD_REQUEST.name());
    }*/
}