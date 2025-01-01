package dev.jora.postman4j;

import dev.jora.postman4j.models.PostmanCollection;
import dev.jora.postman4j.utils.PostmanSettings;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class PostmanMiddlewareFilter extends OncePerRequestFilter {
    private final PostmanSettings settings;

    @Getter
    private final ConcurrentHashMap<String, PostmanCollection> data = new ConcurrentHashMap<>();

    public PostmanMiddlewareFilter() {
        this(PostmanSettings.builder().build());
    }

    public PostmanMiddlewareFilter(PostmanSettings settings) {
        this.settings = settings;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } finally {
            log.info("Request processed: {}", request.getAttributeNames());
        }

    }
}
