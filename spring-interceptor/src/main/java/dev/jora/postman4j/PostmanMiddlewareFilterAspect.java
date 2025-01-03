package dev.jora.postman4j;

import dev.jora.postman4j.annotations.UsePostmanCollection;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.Around;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class PostmanMiddlewareFilterAspect {
    @Around("execution(* *(..)) && @within(dev.jora.postman4j.annotations.UsePostmanCollection)")
    public Object applyUsePostmanCollectionOnEachClassMethod(ProceedingJoinPoint joinPoint) throws Throwable{
        Class<?> targetClass = joinPoint.getTarget().getClass();

        if (targetClass.isAnnotationPresent(UsePostmanCollection.class)) {
            UsePostmanCollection annotation = targetClass.getAnnotation(UsePostmanCollection.class);
            String value = annotation.value();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            request.setAttribute(PostmanMiddlewareFilter.Attribute.COLLECTION_NAME.getValue(), annotation.value());
        }
        return joinPoint.proceed();
    }

    @Pointcut("@annotation(usePostmanCollection)")
    public void enablePostmanMiddlewareFilter(UsePostmanCollection usePostmanCollection) {
        // Pointcut for methods annotated with @UsePostmanCollection
    }

    @Around("enablePostmanMiddlewareFilter(usePostmanCollection)")
    public Object setRequestAttribute(ProceedingJoinPoint joinPoint, UsePostmanCollection usePostmanCollection) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        request.setAttribute(PostmanMiddlewareFilter.Attribute.COLLECTION_NAME.getValue(), usePostmanCollection.value());
        return joinPoint.proceed();
    }
}
