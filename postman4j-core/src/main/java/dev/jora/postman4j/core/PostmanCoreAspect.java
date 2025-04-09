package dev.jora.postman4j.core;

import dev.jora.postman4j.annotations.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author dyadyaJora on 06.04.2025
 * Core aspect that provides unified handling of Postman annotations
 * across all supported client implementations
 */
@Aspect
public class PostmanCoreAspect {
    private static final Logger log = LoggerFactory.getLogger(PostmanCoreAspect.class);
    private final PostmanContextHolder contextHolder = PostmanContextHolder.getInstance();

    private Set<String> getContexts(String[] specifiedContexts) {
        return specifiedContexts.length > 0 ? Set.of(specifiedContexts) : contextHolder.getHolder().keySet();
    }

    private Object handleGeneric(
            ProceedingJoinPoint joinPoint,
            String value,
            String[] contextKeys,
            BiConsumer<IPostmanContext, String> setter,
            Consumer<IPostmanContext> remover
    ) throws Throwable {
        Set<String> contexts = getContexts(contextKeys);
        handleContextOperation(contexts, ctx -> {
            if (ctx.getClientContext().get() != null) {
                setter.accept(ctx.getClientContext().get(), value);
            }
            if (ctx.getServerContext().get() != null) {
                setter.accept(ctx.getServerContext().get(), value);
            }
        });

        try {
            return joinPoint.proceed();
        } finally {
            handleContextOperation(contexts, ctx -> {
                if (ctx.getClientContext().get() != null) {
                    remover.accept(ctx.getClientContext().get());
                }
                if (ctx.getServerContext().get() != null) {
                    remover.accept(ctx.getServerContext().get());
                }
            });
        }
    }

    private void handleContextOperation(Set<String> contexts, Consumer<PostmanContextContainer> operation) {
        contexts.forEach(context -> {
            PostmanContextContainer contextContainer = contextHolder.getContext(context);
            if (contextContainer != null) {
                operation.accept(contextContainer);
            }
        });
    }

    // Handle class-level annotation
    @Around("execution(* *(..)) && @within(dev.jora.postman4j.annotations.UsePostmanCollection)")
    public Object handleClassLevelCollection(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        if (targetClass.isAnnotationPresent(UsePostmanCollection.class)) {
            UsePostmanCollection annotation = targetClass.getAnnotation(UsePostmanCollection.class);
            String name = annotation.value();
            log.debug("Setting class-level collection name: {}", name);

            return handleGeneric(joinPoint,
                    name,
                    annotation.context(),
                    IPostmanContext::setCollectionName,
                    IPostmanContext::removeCollectionName
            );
        }

        return joinPoint.proceed();
    }

    // @UsePostmanCollection handling
    @Pointcut("@annotation(usePostmanCollection)")
    public void usePostmanCollectionPointcut(UsePostmanCollection usePostmanCollection) {}

    @Around("usePostmanCollectionPointcut(usePostmanCollection)")
    public Object handleCollectionName(ProceedingJoinPoint joinPoint, UsePostmanCollection usePostmanCollection) throws Throwable {
        String name = usePostmanCollection.value();
        log.debug("Setting collection name: {}", name);
        return handleGeneric(joinPoint,
                name,
                usePostmanCollection.context(),
                IPostmanContext::setCollectionName,
                IPostmanContext::removeCollectionName
        );
    }

    // @UsePostmanFolderPath handling
    @Pointcut("@annotation(usePostmanFolderPath)")
    public void usePostmanFolderPathPointcut(UsePostmanFolderPath usePostmanFolderPath) {}

    @Around("usePostmanFolderPathPointcut(usePostmanFolderPath)")
    public Object handleFolderPath(ProceedingJoinPoint joinPoint, UsePostmanFolderPath usePostmanFolderPath) throws Throwable {
        String path = usePostmanFolderPath.value();
        log.debug("Setting folder path: {}", path);

        return handleGeneric(joinPoint,
                path,
                usePostmanFolderPath.context(),
                IPostmanContext::setFolderPath,
                IPostmanContext::removeFolderPath
        );
    }

    // @UsePostmanRequest handling
    @Pointcut("@annotation(usePostmanRequest)")
    public void usePostmanRequestPointcut(UsePostmanRequest usePostmanRequest) {}

    @Around("usePostmanRequestPointcut(usePostmanRequest)")
    public Object handleRequestName(ProceedingJoinPoint joinPoint, UsePostmanRequest usePostmanRequest) throws Throwable {
        String name = usePostmanRequest.value();
        log.debug("Setting request name: {}", name);

        return handleGeneric(joinPoint,
                name,
                usePostmanRequest.context(),
                IPostmanContext::setRequestName,
                IPostmanContext::removeRequestName
        );
    }

    // @UsePostmanResponse handling
    @Pointcut("@annotation(usePostmanResponse)")
    public void usePostmanResponsePointcut(UsePostmanResponse usePostmanResponse) {}

    @Around("usePostmanResponsePointcut(usePostmanResponse)")
    public Object handleResponseName(ProceedingJoinPoint joinPoint, UsePostmanResponse usePostmanResponse) throws Throwable {
        String name = usePostmanResponse.value();
        log.debug("Setting response name: {}", name);

        return handleGeneric(joinPoint,
                name,
                usePostmanResponse.context(),
                IPostmanContext::setResponseName,
                IPostmanContext::removeResponseName
        );
    }

    // @AddPostmanFolder handling
    @Pointcut("@annotation(addPostmanFolder)")
    public void addPostmanFolderPointcut(AddPostmanFolder addPostmanFolder) {}

    @Around("addPostmanFolderPointcut(addPostmanFolder)")
    public Object handleAddFolder(ProceedingJoinPoint joinPoint, AddPostmanFolder addPostmanFolder) throws Throwable {
        String name = addPostmanFolder.value();
        log.debug("Adding folder: {}", name);

        return handleGeneric(joinPoint,
                name,
                addPostmanFolder.context(),
                IPostmanContext::addFolder,
                IPostmanContext::removeFolder
        );
    }

}
