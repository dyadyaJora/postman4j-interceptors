package dev.jora.postman4j;

import dev.jora.postman4j.annotations.AddPostmanFolder;
import dev.jora.postman4j.annotations.UsePostmanCollection;
import dev.jora.postman4j.annotations.UsePostmanFolderPath;
import dev.jora.postman4j.annotations.UsePostmanRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author dyadyaJora on 11.12.2024
 */
@Aspect
public class PostmanCollectionAspect {

    @Pointcut("@annotation(usePostmanCollectionAnnotation)")
    public void usePostmanCollectionPointcut(UsePostmanCollection usePostmanCollectionAnnotation) {
        // Pointcut for methods annotated with @UsePostmanCollection
    }

    @Around("usePostmanCollectionPointcut(usePostmanCollectionAnnotation)")
    public Object interceptAnnotatedCollectionMethods(ProceedingJoinPoint joinPoint, UsePostmanCollection usePostmanCollectionAnnotation) throws Throwable {
        String name = usePostmanCollectionAnnotation.value();
        PostmanRequestExecutor.setCollectionName(name);
        try {
            return joinPoint.proceed();
        } finally {
            PostmanRequestExecutor.removeCollectionName();
        }
    }


    @Pointcut("@annotation(usePostmanRequestAnnotation)")
    public void usePostmanRequestPointcut(UsePostmanRequest usePostmanRequestAnnotation) {
        // Pointcut for methods annotated with @UsePostmanRequest
    }

    @Around("usePostmanRequestPointcut(usePostmanRequestAnnotation)")
    public Object interceptAnnotatedRequestMethods(ProceedingJoinPoint joinPoint, UsePostmanRequest usePostmanRequestAnnotation) throws Throwable {
        String name = usePostmanRequestAnnotation.value();
        PostmanRequestExecutor.setRequestName(name);
        try {
            return joinPoint.proceed();
        } finally {
            PostmanRequestExecutor.removeRequestName();
        }
    }

    @Pointcut("@annotation(usePostmanFolderPathAnnotation)")
    public void usePostmanFolderPathPointcut(UsePostmanFolderPath usePostmanFolderPathAnnotation) {
        // Pointcut for methods annotated with @UsePostmanFolderPath
    }

    @Around("usePostmanFolderPathPointcut(usePostmanFolderPathAnnotation)")
    public Object interceptAnnotatedFolderPathMethods(ProceedingJoinPoint joinPoint, UsePostmanFolderPath usePostmanFolderPathAnnotation) throws Throwable {
        String path = usePostmanFolderPathAnnotation.value();
        PostmanRequestExecutor.setFolderPath(path);
        try {
            return joinPoint.proceed();
        } finally {
            PostmanRequestExecutor.removeFolderPath();
        }
    }

    @Pointcut("@annotation(addPostmanFolderAnnotation)")
    public void addPostmanFolderPointcut(AddPostmanFolder addPostmanFolderAnnotation) {
        // Pointcut for methods annotated with @AddPostmanFolder
    }

    @Around("addPostmanFolderPointcut(addPostmanFolderAnnotation)")
    public Object interceptAnnotatedFolderMethods(ProceedingJoinPoint joinPoint, AddPostmanFolder addPostmanFolderAnnotation) throws Throwable {
        String folderName = addPostmanFolderAnnotation.value();
        PostmanRequestExecutor.addFolder(folderName);
        try {
            return joinPoint.proceed();
        } finally {
            PostmanRequestExecutor.removeFolder();
        }
    }


}
