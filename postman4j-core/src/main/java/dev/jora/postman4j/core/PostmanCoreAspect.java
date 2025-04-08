package dev.jora.postman4j.core;

import dev.jora.postman4j.annotations.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author dyadyaJora on 06.04.2025
 * Core aspect that provides unified handling of Postman annotations
 * across all supported client implementations
 */
@Aspect
public class PostmanCoreAspect {
    private static final Logger log = LoggerFactory.getLogger(PostmanCoreAspect.class);
    private final PostmanContextHolder contextHolder = PostmanContextHolder.getInstance();

    // @UsePostmanCollection handling
    @Pointcut("@annotation(usePostmanCollection)")
    public void usePostmanCollectionPointcut(UsePostmanCollection usePostmanCollection) {}

    @Around("usePostmanCollectionPointcut(usePostmanCollection)")
    public Object handleCollectionName(ProceedingJoinPoint joinPoint, UsePostmanCollection usePostmanCollection) throws Throwable {
        String name = usePostmanCollection.value();
        log.debug("Setting collection name: {}", name);

        Set<String> contextsToGet;
        if (usePostmanCollection.context().length > 0) {
            contextsToGet = Set.of(usePostmanCollection.context());
        } else {
            contextsToGet = contextHolder.getHolder().keySet();
        }

        contextsToGet.forEach(context -> {
            PostmanContextContainer contextContainer = contextHolder.getContext(context);
            if (contextContainer == null) {
                return;
            }
            if (contextContainer.getClientContext().get() != null) {
                contextContainer.getClientContext().get().setCollectionName(name);
            }
            if (contextContainer.getServerContext().get() != null) {
                contextContainer.getServerContext().get().setCollectionName(name);
            }
        });

        try {
            return joinPoint.proceed();
        } finally {
            contextsToGet.forEach(context -> {
                PostmanContextContainer contextContainer = contextHolder.getContext(context);
                if (contextContainer == null) {
                    return;
                }
                if (contextContainer.getClientContext().get() != null) {
                    contextContainer.getClientContext().get().removeCollectionName();
                }
                if (contextContainer.getServerContext().get() != null) {
                    contextContainer.getServerContext().get().removeCollectionName();
                }
            });
        }
    }

    // @UsePostmanFolderPath handling
    @Pointcut("@annotation(usePostmanFolderPath)")
    public void usePostmanFolderPathPointcut(UsePostmanFolderPath usePostmanFolderPath) {}

    @Around("usePostmanFolderPathPointcut(usePostmanFolderPath)")
    public Object handleFolderPath(ProceedingJoinPoint joinPoint, UsePostmanFolderPath usePostmanFolderPath) throws Throwable {
        String path = usePostmanFolderPath.value();
        log.debug("Setting folder path: {}", path);

        Set<String> contextsToGet;
        if (usePostmanFolderPath.context().length > 0) {
            contextsToGet = Set.of(usePostmanFolderPath.context());
        } else {
            contextsToGet = contextHolder.getHolder().keySet();
        }

        contextsToGet.forEach(context -> {
            PostmanContextContainer contextContainer = contextHolder.getContext(context);
            if (contextContainer == null) {
                return;
            }
            if (contextContainer.getClientContext().get() != null) {
                contextContainer.getClientContext().get().setFolderPath(path);
            }
            if (contextContainer.getServerContext().get() != null) {
                contextContainer.getServerContext().get().setFolderPath(path);
            }
        });

        try {
            return joinPoint.proceed();
        } finally {
            contextsToGet.forEach(context -> {
                PostmanContextContainer contextContainer = contextHolder.getContext(context);
                if (contextContainer == null) {
                    return;
                }
                if (contextContainer.getClientContext().get() != null) {
                    contextContainer.getClientContext().get().removeFolderPath();
                }
                if (contextContainer.getServerContext().get() != null) {
                    contextContainer.getServerContext().get().removeFolderPath();
                }
            });
        }
    }

    // @UsePostmanRequest handling
    @Pointcut("@annotation(usePostmanRequest)")
    public void usePostmanRequestPointcut(UsePostmanRequest usePostmanRequest) {}

    @Around("usePostmanRequestPointcut(usePostmanRequest)")
    public Object handleRequestName(ProceedingJoinPoint joinPoint, UsePostmanRequest usePostmanRequest) throws Throwable {
        String name = usePostmanRequest.value();
        log.debug("Setting request name: {}", name);

        Set<String> contextsToGet;
        if (usePostmanRequest.context().length > 0) {
            contextsToGet = Set.of(usePostmanRequest.context());
        } else {
            contextsToGet = contextHolder.getHolder().keySet();
        }

        contextsToGet.forEach(context -> {
            PostmanContextContainer contextContainer = contextHolder.getContext(context);
            if (contextContainer == null) {
                return;
            }
            if (contextContainer.getClientContext().get() != null) {
                contextContainer.getClientContext().get().setRequestName(name);
            }
            if (contextContainer.getServerContext().get() != null) {
                contextContainer.getServerContext().get().setRequestName(name);
            }
        });

        try {
            return joinPoint.proceed();
        } finally {
            contextsToGet.forEach(context -> {
                PostmanContextContainer contextContainer = contextHolder.getContext(context);
                if (contextContainer == null) {
                    return;
                }
                if (contextContainer.getClientContext().get() != null) {
                    contextContainer.getClientContext().get().removeRequestName();
                }
                if (contextContainer.getServerContext().get() != null) {
                    contextContainer.getServerContext().get().removeRequestName();
                }
            });
        }
    }

    // @UsePostmanResponse handling
    @Pointcut("@annotation(usePostmanResponse)")
    public void usePostmanResponsePointcut(UsePostmanResponse usePostmanResponse) {}

    @Around("usePostmanResponsePointcut(usePostmanResponse)")
    public Object handleResponseName(ProceedingJoinPoint joinPoint, UsePostmanResponse usePostmanResponse) throws Throwable {
        String name = usePostmanResponse.value();
        log.debug("Setting response name: {}", name);

        Set<String> contextsToGet;
        if (usePostmanResponse.context().length > 0) {
            contextsToGet = Set.of(usePostmanResponse.context());
        } else {
            contextsToGet = contextHolder.getHolder().keySet();
        }

        contextsToGet.forEach(context -> {
            PostmanContextContainer contextContainer = contextHolder.getContext(context);
            if (contextContainer == null) {
                return;
            }
            if (contextContainer.getClientContext().get() != null) {
                contextContainer.getClientContext().get().setResponseName(name);
            }
            if (contextContainer.getServerContext().get() != null) {
                contextContainer.getServerContext().get().setResponseName(name);
            }
        });

        try {
            return joinPoint.proceed();
        } finally {
            contextsToGet.forEach(context -> {
                PostmanContextContainer contextContainer = contextHolder.getContext(context);
                if (contextContainer == null) {
                    return;
                }
                if (contextContainer.getClientContext().get() != null) {
                    contextContainer.getClientContext().get().removeResponseName();
                }
                if (contextContainer.getServerContext().get() != null) {
                    contextContainer.getServerContext().get().removeResponseName();
                }
            });
        }
    }

    // @AddPostmanFolder handling
    @Pointcut("@annotation(addPostmanFolder)")
    public void addPostmanFolderPointcut(AddPostmanFolder addPostmanFolder) {}

    @Around("addPostmanFolderPointcut(addPostmanFolder)")
    public Object handleAddFolder(ProceedingJoinPoint joinPoint, AddPostmanFolder addPostmanFolder) throws Throwable {
        String name = addPostmanFolder.value();
        log.debug("Adding folder: {}", name);

        Set<String> contextsToGet;
        if (addPostmanFolder.context().length > 0) {
            contextsToGet = Set.of(addPostmanFolder.context());
        } else {
            contextsToGet = contextHolder.getHolder().keySet();
        }

        contextsToGet.forEach(context -> {
            PostmanContextContainer contextContainer = contextHolder.getContext(context);
            if (contextContainer == null) {
                return;
            }
            if (contextContainer.getClientContext().get() != null) {
                contextContainer.getClientContext().get().addFolder(name);
            }
            if (contextContainer.getServerContext().get() != null) {
                contextContainer.getServerContext().get().addFolder(name);
            }
        });

        try {
            return joinPoint.proceed();
        } finally {
            contextsToGet.forEach(context -> {
                PostmanContextContainer contextContainer = contextHolder.getContext(context);
                if (contextContainer == null) {
                    return;
                }
                if (contextContainer.getClientContext().get() != null) {
                    contextContainer.getClientContext().get().removeFolder();
                }
                if (contextContainer.getServerContext().get() != null) {
                    contextContainer.getServerContext().get().removeFolder();
                }
            });
        }
    }

    // Handle class-level annotation
//    @Pointcut("execution(* *(..)) && @within(usePostmanCollection)")
//    public void classWithPostmanCollection(UsePostmanCollection usePostmanCollection) {}
//
//    @Around("classWithPostmanCollection(usePostmanCollection)")
//    public Object handleClassLevelCollection(ProceedingJoinPoint joinPoint, UsePostmanCollection usePostmanCollection) throws Throwable {
//        String name = usePostmanCollection.value();
//        log.debug("Setting class-level collection name: {}", name);
//
//        contextHolder.setCollectionName(name);
//        try {
//            return joinPoint.proceed();
//        } finally {
//            contextHolder.removeCollectionName();
//            contextHolder.clearContext();
//        }
//    }
}
