package edu.java.scrapper.DB.jdbc;

import edu.java.DB.jdbc.DAO.ChatDAO;
import edu.java.DB.jdbc.DAO.ChatLinkDAO;
import edu.java.DB.jdbc.DAO.LinkDAO;
import edu.java.DB.jdbc.DTO.ChatDTO;
import edu.java.DB.jdbc.DTO.LinkDTO;
import edu.java.scrapper.IntegrationTest;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class ChatDAOTest extends IntegrationTest {
    private final ChatDAO connectionChat;
    private final LinkDAO connectionLink;
    private final ChatLinkDAO connectionChatLink;

    @Autowired
    ChatDAOTest(DataSource dataSource) {
        connectionChat = new ChatDAO(dataSource);
        connectionLink = new LinkDAO(dataSource);
        connectionChatLink = new ChatLinkDAO(dataSource);
    }

    @Transactional
    @Rollback
    @Test
    void addChat() {
        connectionChat.addChat(1234567L);
        connectionChat.addChat(7654321L);
    }

    @Transactional
    @Rollback
    @Test
    void findAllChats() {
        connectionChat.addChat(1234567L);
        connectionChat.addChat(7654321L);

        List<ChatDTO> expected = new ArrayList<>();
        expected.add(new ChatDTO(1L, 1234567L));
        expected.add(new ChatDTO(2L, 7654321L));

        List<ChatDTO> result = connectionChat.findAllChats();

        assertThat(result.size()).isEqualTo(expected.size());
        assertThat(result.get(0))
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(expected.get(0));
        assertThat(result.get(1))
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(expected.get(1));
    }

    @Transactional
    @Rollback
    @Test
    void removeChat() {
        connectionChat.addChat(1234567L);

        connectionChat.removeChat(1234567L);
    }

    @Transactional
    @Rollback
    @Test
    void findChatId() {
        connectionChat.addChat(1234567L);

        Long result = connectionChat.findChatId(1234567L);

        assertNotNull(result);
    }

    @Transactional
    @Rollback
    @Test
    void findAllLinksForChat() {
        connectionChat.addChat(1234567L);
        connectionLink.addLink(
            URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"),
            OffsetDateTime.now());
        connectionLink.addLink(
            URI.create("https://api.stackexchange.com/2.2/questions"
                + "/123456?order=desc&sort=activity&site=stackoverflow"),
            OffsetDateTime.now());
        long idChat = connectionChat.findChatId(1234567L);
        LinkDTO firstLink = connectionLink.findLinkByURI(
            URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"));
        LinkDTO secondLink = connectionLink.findLinkByURI(
            URI.create("https://api.stackexchange.com/2.2/questions"
                + "/123456?order=desc&sort=activity&site=stackoverflow"));
        connectionChatLink.addChatLink(idChat, firstLink.id());
        connectionChatLink.addChatLink(idChat, secondLink.id());

        List<LinkDTO> expected = new ArrayList<>();
        expected.add(firstLink);
        expected.add(secondLink);

        List<LinkDTO> result = connectionChat.findAllLinksForChat(1234567L);

        assertThat(result).isEqualTo(expected);
    }
}
