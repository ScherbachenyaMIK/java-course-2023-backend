package edu.java.scrapper.DB;

import edu.java.scrapper.IntegrationTest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.PostgreSQLContainer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RunDBTest {
    private static final PostgreSQLContainer<?> CONTAINER = IntegrationTest.POSTGRES;

    private final Connection connection;

    public RunDBTest() throws SQLException {
        connection = DriverManager.getConnection(
            CONTAINER.getJdbcUrl(),
            CONTAINER.getUsername(),
            CONTAINER.getPassword());
    }

    @Test
    public void containerStartup() {
        assertTrue(CONTAINER.isRunning());
    }

    @Test
    public void checkChatTable() throws SQLException {
        long expectedChatId = 1L;
        String expectedUsername = "ScherbachenyaMIK";

        PreparedStatement insertStatement = connection.prepareStatement(
            "INSERT INTO Chat (username) VALUES (?)");
            insertStatement.setString(1, expectedUsername);
            insertStatement.executeUpdate();

        ResultSet resultSet = connection.prepareStatement("SELECT * FROM Chat;").executeQuery();
        assertTrue(resultSet.next());
        long resultId = resultSet.getLong("id");
        String resultUsername = resultSet.getString("username");
        assertEquals(expectedChatId, resultId);
        assertEquals(expectedUsername, resultUsername);
        assertFalse(resultSet.next());
    }

    @Test
    public void checkLinkTable() throws SQLException {
        long expectedLinkId = 1L;
        String expectedLink = "https://github.com/ScherbachenyaMIK/java-course-2023-backend";
        OffsetDateTime expectedTime = OffsetDateTime.now();

        PreparedStatement insertStatement = connection.prepareStatement(
            "INSERT INTO Link (link, last_update) VALUES (?, ?);");
        insertStatement.setString(1, expectedLink);
        insertStatement.setTimestamp(2, Timestamp.from(expectedTime.toInstant()));
        insertStatement.executeUpdate();

        ResultSet resultSet = connection.prepareStatement("SELECT * FROM Link;").executeQuery();
        assertTrue(resultSet.next());
        long resultId = resultSet.getLong("id");
        String resultLink = resultSet.getString("link");
        OffsetDateTime resultTime = OffsetDateTime.ofInstant(
            resultSet.getTimestamp("last_update").toInstant(),
            ZoneId.systemDefault()
        );
        assertEquals(expectedLinkId, resultId);
        assertEquals(expectedLink, resultLink);
        assertEquals(expectedTime.truncatedTo(ChronoUnit.SECONDS),
            resultTime.truncatedTo(ChronoUnit.SECONDS));
        assertFalse(resultSet.next());
    }

    @Test
    public void checkChat_LinkTable() throws SQLException {
        long expectedLinkId = 1L;
        long expectedChatId = 1L;

        PreparedStatement insertStatement = connection.prepareStatement(
            "INSERT INTO Chat_Link (chat_id, link_id) VALUES (?, ?);");
        insertStatement.setLong(1, expectedLinkId);
        insertStatement.setLong(2, expectedChatId);
        insertStatement.executeUpdate();

        ResultSet resultSet = connection.prepareStatement("SELECT * FROM Chat_Link;").executeQuery();
        assertTrue(resultSet.next());
        long resultLinkId = resultSet.getLong("link_id");
        long resultChatId = resultSet.getLong("chat_id");
        assertEquals(expectedLinkId, resultLinkId);
        assertEquals(expectedChatId, resultChatId);
        assertFalse(resultSet.next());
    }

    @Test
    @Order(value = Integer.MAX_VALUE)
    public void joinTest() throws SQLException {
        long expectedLinkId = 1L;
        String expectedLink = "https://github.com/ScherbachenyaMIK/java-course-2023-backend";
        OffsetDateTime expectedTime = OffsetDateTime.now();
        String username = "ScherbachenyaMIK";

        PreparedStatement selectionStatement = connection.prepareStatement(
            """
                    SELECT *
                    FROM Chat c
                    JOIN Chat_Link cl ON c.id = cl.chat_id
                    JOIN Link l ON cl.link_id = l.id
                    WHERE c.username = (?);
                    """);
        selectionStatement.setString(1, username);
        ResultSet resultSet = selectionStatement.executeQuery();
        assertTrue(resultSet.next());
        long resultId = resultSet.getLong("id");
        String resultLink = resultSet.getString("link");
        OffsetDateTime resultTime = OffsetDateTime.ofInstant(
            resultSet.getTimestamp("last_update").toInstant(),
            ZoneId.systemDefault()
        );
        assertEquals(expectedLinkId, resultId);
        assertEquals(expectedLink, resultLink);
        assertEquals(expectedTime.truncatedTo(ChronoUnit.SECONDS),
            resultTime.truncatedTo(ChronoUnit.SECONDS));
        assertFalse(resultSet.next());
    }
}
