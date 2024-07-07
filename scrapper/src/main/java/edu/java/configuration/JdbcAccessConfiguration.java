package edu.java.configuration;

import edu.java.service.jdbc.JdbcLinkService;
import edu.java.service.jdbc.JdbcTgChatService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {
    @Bean
    public JdbcTgChatService jdbcTgChatService() {
        return new JdbcTgChatService();
    }

    @Bean
    public JdbcLinkService jdbcLinkService() {
        return new JdbcLinkService();
    }
}
