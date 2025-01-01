package dev.jora.postman4j;

import dev.jora.postman4j.annotations.UsePostmanCollection;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.Around;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class PostmanMiddlewareFilterAspect {


    @Pointcut("@annotation(usePostmanCollection)")
    public void enablePostmanMiddlewareFilter(UsePostmanCollection usePostmanCollection) {
        // Pointcut for methods annotated with @EnablePostmanMiddlewareFilter
    }

    @Around("enablePostmanMiddlewareFilter(usePostmanCollection)")
    public Object setRequestAttribute(ProceedingJoinPoint joinPoint, UsePostmanCollection usePostmanCollection) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        request.setAttribute("usePostmanCollection", usePostmanCollection.value());
        Object[] args = joinPoint.getArgs();

        for (Object arg : args) {
            System.out.println(arg);
        }
        return joinPoint.proceed();
    }
}
