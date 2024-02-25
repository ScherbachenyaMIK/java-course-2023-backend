package edu.java.scrapper.util;

import java.io.IOException;
import java.util.Properties;

import edu.java.util.ClientErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientErrorCodeTest {
    private ClientErrorCode commandErrorCode;
    private Properties properties;

    @BeforeEach
    void setUp() throws IOException {
        Resource resource = new ClassPathResource("errorDescription.yml");
        properties = PropertiesLoaderUtils.loadProperties(resource);
        commandErrorCode = new ClientErrorCode();
    }

    @Test
    void getCommandErrorCodeForCode1() {
        assertEquals(properties.getProperty("EXIT_CODE_1"),
            commandErrorCode.getClientErrorCode("EXIT_CODE_1"));
    }

    @Test
    void getCommandErrorCodeForCode2() {
        assertEquals(properties.getProperty("EXIT_CODE_2"),
            commandErrorCode.getClientErrorCode("EXIT_CODE_2"));
    }

    @Test
    void getCommandErrorCodeForCode3() {
        assertEquals(properties.getProperty("EXIT_CODE_3"),
            commandErrorCode.getClientErrorCode("EXIT_CODE_3"));
    }

    @Test
    void getCommandErrorCodeForCode4() {
        assertEquals(properties.getProperty("EXIT_CODE_4"),
            commandErrorCode.getClientErrorCode("EXIT_CODE_4"));
    }
}
