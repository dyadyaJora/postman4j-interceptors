package dev.jora.postman4j;

import dev.jora.postman4j.endpoints.PostmanActuatorEndpoint;
import dev.jora.postman4j.utils.PostmanSettings;
import dev.jora.postman4j.utils.RequestResponseMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author dyadyaJora on 30.12.2024
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<PostmanMiddlewareFilter> postmanMiddlewareFilterRegistration(@Autowired PostmanMiddlewareFilter postmanMiddlewareFilter) {
        FilterRegistrationBean<PostmanMiddlewareFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(postmanMiddlewareFilter);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Bean
    @Profile("!test")
    public PostmanSettings postmanSettings() {
        return PostmanSettings.builder()
                .requestResponseMode(RequestResponseMode.REQUEST_AND_RESPONSE)
                .build();
    }

    @Bean
    public PostmanMiddlewareFilter postmanMiddlewareFilter(@Autowired PostmanSettings postmanSettings) {
        return new PostmanMiddlewareFilter(postmanSettings);
    }

    @Bean
    public PostmanActuatorEndpoint postmanActuatorEndpoint(@Autowired PostmanMiddlewareFilter postmanMiddlewareFilter) {
        return new PostmanActuatorEndpoint(postmanMiddlewareFilter);
    }
}
