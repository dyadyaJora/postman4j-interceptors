package dev.jora.postman4j;

import dev.jora.postman4j.annotations.UsePostmanCollection;
import dev.jora.postman4j.annotations.UsePostmanFolderPath;
import dev.jora.postman4j.annotations.UsePostmanRequest;
import dev.jora.postman4j.annotations.UsePostmanResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.Around;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
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
        HttpServletRequest request = extractRequestAttributes();
        if (request != null) {
            request.setAttribute(PostmanMiddlewareFilter.Attribute.COLLECTION_NAME.getValue(), usePostmanCollection.value());
        } else {
            log.warn("Request is null in {}, cannot set attribute", joinPoint.getSignature());
        }
        return joinPoint.proceed();
    }

    @Pointcut("@annotation(usePostmanFolderPath)")
    public void enablePostmanFolderPathMiddlewareFilter(UsePostmanFolderPath usePostmanFolderPath) {
        // Pointcut for methods annotated with @UsePostmanFolderPath
    }

    @Around("enablePostmanFolderPathMiddlewareFilter(usePostmanFolderPath)")
    public Object setFolderPathRequestAttribute(ProceedingJoinPoint joinPoint, UsePostmanFolderPath usePostmanFolderPath) throws Throwable {
        HttpServletRequest request = extractRequestAttributes();
        if (request != null) {
            request.setAttribute(PostmanMiddlewareFilter.Attribute.FOLDER_PATH.getValue(), usePostmanFolderPath.value());
        } else {
            log.warn("Request is null in {}, cannot set attribute", joinPoint.getSignature());
        }
        return joinPoint.proceed();
    }

    @Pointcut("@annotation(usePostmanRequest)")
    public void enablePostmanRequestMiddlewareFilter(UsePostmanRequest usePostmanRequest) {
        // Pointcut for methods annotated with @UsePostmanRequest
    }

    @Around("enablePostmanRequestMiddlewareFilter(usePostmanRequest)")
    public Object setRequestRequestAttribute(ProceedingJoinPoint joinPoint, UsePostmanRequest usePostmanRequest) throws Throwable {
        HttpServletRequest request = extractRequestAttributes();
        if (request != null) {
            request.setAttribute(PostmanMiddlewareFilter.Attribute.REQUEST_NAME.getValue(), usePostmanRequest.value());
        } else {
            log.warn("Request is null in {}, cannot set attribute", joinPoint.getSignature());
        }
        return joinPoint.proceed();
    }

    @Pointcut("@annotation(usePostmanResponse)")
    public void enablePostmanResponseMiddlewareFilter(UsePostmanResponse usePostmanResponse) {
        // Pointcut for methods annotated with @UsePostmanResponse
    }

    @Around("enablePostmanResponseMiddlewareFilter(usePostmanResponse)")
    public Object setResponseRequestAttribute(ProceedingJoinPoint joinPoint, UsePostmanResponse usePostmanResponse) throws Throwable {
        HttpServletRequest request = extractRequestAttributes();
        if (request != null) {
            request.setAttribute(PostmanMiddlewareFilter.Attribute.RESPONSE_NAME.getValue(), usePostmanResponse.value());
        } else {
            log.warn("Request is null in {}, cannot set attribute", joinPoint.getSignature());
        }
        return joinPoint.proceed();
    }

    private HttpServletRequest extractRequestAttributes() {
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if (servletRequestAttributes == null) {
            return null;
        }
        return servletRequestAttributes.getRequest();
    }
}
