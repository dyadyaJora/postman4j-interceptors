package dev.jora.postman4j.core;

import dev.jora.postman4j.utils.PostmanSettings;
import lombok.Getter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author dyadyaJora on 06.04.2025
 */
public class PostmanContextHolder {
    @Getter
    private final ConcurrentHashMap<String, PostmanContextContainer> holder = new ConcurrentHashMap<>();
    private static final PostmanContextHolder INSTANCE = new PostmanContextHolder();

    public PostmanContextContainer getContext(String name) {
        return holder.computeIfAbsent(name, k -> new PostmanContextContainer());
    }

    public PostmanContextContainer getContext() {
        return getContext(IPostmanContext.DEFAULT_CONTEXT_ID);
    }

    public IPostmanContext getClientContext(String contextId, PostmanSettings settings) {
        if (contextId == null) {
            contextId = IPostmanContext.DEFAULT_CONTEXT_ID;
        }

        return holder.compute(contextId, (key, container) -> {
            if (container == null) {
                container = new PostmanContextContainer();
            }
            container.getClientContext().compareAndSet(null, new PostmanContext());
            if (settings != null) {
                container.getClientContext().get().setSettings(settings);
            }
            return container;
        }).getClientContext().get();
    }

    public IPostmanContext getClientContext(PostmanSettings settings) {
        return this.getClientContext(IPostmanContext.DEFAULT_CONTEXT_ID, settings);
    }

    public IPostmanContext getClientContext() {
        return this.getClientContext(IPostmanContext.DEFAULT_CONTEXT_ID, null);
    }

    public IPostmanContext getServerContext(String contextId, PostmanSettings settings, Supplier<IPostmanContext> supplier) {
        if (contextId == null) {
            contextId = IPostmanContext.DEFAULT_CONTEXT_ID;
        }

        return holder.compute(contextId, (key, container) -> {
            if (container == null) {
                container = new PostmanContextContainer();
            }
            container.getServerContext().compareAndSet(null, supplier.get());
            if (settings != null) {
                container.getServerContext().get().setSettings(settings);
            }
            return container;
        }).getServerContext().get();
    }


    public static PostmanContextHolder getInstance() {
        return INSTANCE;
    }
}
