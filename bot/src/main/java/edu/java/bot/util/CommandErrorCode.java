package edu.java.bot.util;

import java.io.IOException;
import java.util.Properties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

@Component
public class CommandErrorCode {
    private final Properties properties;

    CommandErrorCode() throws IOException {
        Resource resource = new ClassPathResource("errorDescription.yml");
        properties = PropertiesLoaderUtils.loadProperties(resource);
    }

    public String getCommandErrorCode(String exitCode) {
        return properties.getProperty(String.valueOf(exitCode));
    }
}
