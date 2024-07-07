package edu.java.configuration;

import edu.java.service.jpa.JpaLinkService;
import edu.java.service.jpa.JpaTgChatService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfiguration {
    @Bean
    public JpaTgChatService jpaTgChatService() {
        return new JpaTgChatService();
    }

    @Bean
    public JpaLinkService jpaLinkService() {
        return new JpaLinkService();
    }
}
