package edu.java.scrapper.DB;

import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.PostgreSQLContainer;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RunDBTest {
    private static final PostgreSQLContainer<?> CONTAINER = IntegrationTest.POSTGRES;

    @Test
    public void containerStartup() {
        assertTrue(CONTAINER.isRunning());
    }
}
