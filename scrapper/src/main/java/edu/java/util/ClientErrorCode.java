package edu.java.util;

import java.io.IOException;
import java.util.Properties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

@Component
public class ClientErrorCode {
    private final Properties properties;

    public ClientErrorCode() throws IOException {
        Resource resource = new ClassPathResource("errorDescription.yml");
        properties = PropertiesLoaderUtils.loadProperties(resource);
    }

    public String getClientErrorCode(String exitCode) {
        return properties.getProperty(exitCode);
    }
}
