package edu.java.scrapper.DB.jdbc;

import edu.java.DB.jdbc.DAO.ChatDAO;
import edu.java.DB.jdbc.DAO.ChatLinkDAO;
import edu.java.DB.jdbc.DAO.LinkDAO;
import edu.java.DB.jdbc.DTO.ChatDTO;
import edu.java.DB.jdbc.DTO.ChatLinkDTO;
import edu.java.DB.jdbc.DTO.LinkDTO;
import edu.java.scrapper.IntegrationTest;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class ChatLinkDAOTest extends IntegrationTest {
    private final ChatLinkDAO connectionChatLink;
    private final ChatDAO connectionChat;
    private final LinkDAO connectionLink;

    @Autowired
    ChatLinkDAOTest(DataSource dataSource) {
        connectionLink = new LinkDAO(dataSource);
        connectionChat = new ChatDAO(dataSource);
        connectionChatLink = new ChatLinkDAO(dataSource);
    }

    @Order(value = 1)
    @Transactional
    @Rollback
    @Test
    void addChatLink() throws URISyntaxException {
        connectionLink.addLink(
            new URI("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"),
            OffsetDateTime.of(
                2024,
                3,
                15,
                18,
                0,
                0,
                0,
                ZoneOffset.UTC
            ));
        connectionLink.addLink(
            new URI("https://api.stackexchange.com/2.2/questions" +
                "/78110387?order=desc&sort=activity&site=stackoverflow"),
            OffsetDateTime.of(
                2024,
                2,
                16,
                14,
                0,
                0,
                0,
                ZoneOffset.UTC
            ));

        connectionChat.addChat(1234567L);
        connectionChat.addChat(7654321L);

        List<LinkDTO> Links = connectionLink.findAllLinks();
        List<ChatDTO> Chats = connectionChat.findAllChats();

        connectionChatLink.addChatLink(Chats.get(0).id(), Links.get(0).id());
        connectionChatLink.addChatLink(Chats.get(0).id(), Links.get(1).id());
        connectionChatLink.addChatLink(Chats.get(1).id(), Links.get(1).id());
    }

    @Order(value = 2)
    @Transactional
    @Rollback
    @Test
    void findAllChatLinks() throws URISyntaxException {
        connectionLink.addLink(
            new URI("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"),
            OffsetDateTime.of(
                2024,
                3,
                15,
                18,
                0,
                0,
                0,
                ZoneOffset.UTC
            ));
        connectionLink.addLink(
            new URI("https://api.stackexchange.com/2.2/questions" +
                "/78110387?order=desc&sort=activity&site=stackoverflow"),
            OffsetDateTime.of(
                2024,
                2,
                16,
                14,
                0,
                0,
                0,
                ZoneOffset.UTC
            ));

        connectionChat.addChat(1234567L);
        connectionChat.addChat(7654321L);

        List<LinkDTO> Links = connectionLink.findAllLinks();
        List<ChatDTO> Chats = connectionChat.findAllChats();

        connectionChatLink.addChatLink(Chats.get(0).id(), Links.get(0).id());
        connectionChatLink.addChatLink(Chats.get(0).id(), Links.get(1).id());
        connectionChatLink.addChatLink(Chats.get(1).id(), Links.get(1).id());

        List<ChatLinkDTO> expected = new ArrayList<>();
        expected.add(new ChatLinkDTO(Chats.get(0).id(), Links.get(0).id()));
        expected.add(new ChatLinkDTO(Chats.get(0).id(), Links.get(1).id()));
        expected.add(new ChatLinkDTO(Chats.get(1).id(), Links.get(1).id()));

        List<ChatLinkDTO> result = connectionChatLink.findAllChatLinks();

        assertThat(result).isEqualTo(expected);
    }

    @Order(value = 3)
    @Transactional
    @Rollback
    @Test
    void removeChatLink() throws URISyntaxException {
        connectionLink.addLink(
            new URI("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"),
            OffsetDateTime.of(
                2024,
                3,
                15,
                18,
                0,
                0,
                0,
                ZoneOffset.UTC
            ));
        connectionLink.addLink(
            new URI("https://api.stackexchange.com/2.2/questions" +
                "/78110387?order=desc&sort=activity&site=stackoverflow"),
            OffsetDateTime.of(
                2024,
                2,
                16,
                14,
                0,
                0,
                0,
                ZoneOffset.UTC
            ));

        connectionChat.addChat(1234567L);
        connectionChat.addChat(7654321L);

        List<LinkDTO> Links = connectionLink.findAllLinks();
        List<ChatDTO> Chats = connectionChat.findAllChats();

        connectionChatLink.addChatLink(Chats.get(0).id(), Links.get(0).id());
        connectionChatLink.addChatLink(Chats.get(0).id(), Links.get(1).id());
        connectionChatLink.addChatLink(Chats.get(1).id(), Links.get(1).id());

        connectionChatLink.removeChatLink(1L, 1L);
        connectionChatLink.removeChatLink(1L, 2L);
        connectionChatLink.removeChatLink(2L, 2L);
    }
}
