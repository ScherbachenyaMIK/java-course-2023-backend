package edu.java.scrapper.DB.jdbc;

import edu.java.DB.jdbc.DAO.ChatDAO;
import edu.java.DB.jdbc.DAO.ChatLinkDAO;
import edu.java.DB.jdbc.DAO.LinkDAO;
import edu.java.DB.jdbc.DTO.ChatDTO;
import edu.java.DB.jdbc.DTO.LinkDTO;
import edu.java.scrapper.IntegrationTest;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class LinkDAOTest extends IntegrationTest {
    private final ChatDAO connectionChat;
    private final LinkDAO connectionLink;
    private final ChatLinkDAO connectionChatLink;

    @Autowired
    LinkDAOTest(DataSource dataSource) {
        connectionChat = new ChatDAO(dataSource);
        connectionLink = new LinkDAO(dataSource);
        connectionChatLink = new ChatLinkDAO(dataSource);
    }

    @Transactional
    @Rollback
    @Test
    void addLink() throws URISyntaxException {
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
    }

    @Transactional
    @Rollback
    @Test
    void findAllLinks() throws URISyntaxException {
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

        List<LinkDTO> expected = new ArrayList<>();
        expected.add(new LinkDTO(
            1L,
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
            ),
            OffsetDateTime.now()
        ));
        expected.add(new LinkDTO(
            2L,
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
            ),
            OffsetDateTime.now()
        ));

        List<LinkDTO> result = connectionLink.findAllLinks();

        assertThat(result.size()).isEqualTo(expected.size());
        assertThat(result.get(0))
            .usingRecursiveComparison()
            .ignoringFields("id", "lastSeen")
            .isEqualTo(expected.get(0));
        assertThat(result.get(1))
            .usingRecursiveComparison()
            .ignoringFields("id", "lastSeen")
            .isEqualTo(expected.get(1));
    }

    @Transactional
    @Rollback
    @Test
    void removeLink() throws URISyntaxException {
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

        connectionLink.removeLink(new URI("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"));
    }

    @Transactional
    @Rollback
    @Test
    void findAllLinksWithFilter() {
        OffsetDateTime now = OffsetDateTime.now();
        connectionLink.addLink(
            URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"),
            now);
        connectionLink.addLink(
            URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend2"),
            now);
        connectionLink.addLink(
            URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend3"),
            now);
        LinkDTO firstLink = connectionLink.findLinkByURI(
            URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend2"));
        LinkDTO secondLink = connectionLink.findLinkByURI(
            URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend3"));
        LinkDTO thirdLink = connectionLink.findLinkByURI(
            URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"));

        connectionLink.updateLink(firstLink.id(), now, now.minusMinutes(7));
        connectionLink.updateLink(secondLink.id(), now, now.minusMinutes(8));
        connectionLink.updateLink(thirdLink.id(), now, now.plusHours(1));

        firstLink = connectionLink.findLinkByURI(
            URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend2"));
        secondLink = connectionLink.findLinkByURI(
            URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend3"));

        List<LinkDTO> expected = new ArrayList<>();
        expected.add(firstLink);
        expected.add(secondLink);

        List<LinkDTO> result = connectionLink.findAllLinksWithFilter();

        assertThat(result).isEqualTo(expected);
    }

    @Transactional
    @Rollback
    @Test
    void findAllChatsForLink() {
        connectionLink.addLink(
            URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"),
            OffsetDateTime.now().plusHours(1));
        LinkDTO link = connectionLink.findLinkByURI(
            URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"));

        connectionChat.addChat(1L);
        connectionChat.addChat(2L);
        connectionChat.addChat(3L);
        long firstChatId = connectionChat.findChatId(1L);
        long secondChatId = connectionChat.findChatId(2L);
        long thirdChatId = connectionChat.findChatId(3L);

        connectionChatLink.addChatLink(firstChatId, link.id());
        connectionChatLink.addChatLink(secondChatId, link.id());
        connectionChatLink.addChatLink(thirdChatId, link.id());

        List<ChatDTO> expected = new ArrayList<>();
        expected.add(new ChatDTO(firstChatId, 1L));
        expected.add(new ChatDTO(secondChatId, 2L));
        expected.add(new ChatDTO(thirdChatId, 3L));

        List<ChatDTO> result = connectionLink.findAllChatsForLink(link.id());

        assertThat(result).isEqualTo(expected);
    }

    @Transactional
    @Rollback
    @Test
    void updateLink() {
        connectionLink.addLink(
            URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"),
            OffsetDateTime.now().minusHours(1));
        LinkDTO link = connectionLink.findLinkByURI(
            URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"));
        OffsetDateTime now = OffsetDateTime.now();

        LinkDTO expected = new LinkDTO(
            link.id(),
            link.url(),
            now,
            now
        );

        assertFalse(link.lastUpdate().until(now, ChronoUnit.SECONDS) <= 1);

        connectionLink.updateLink(link.id(), now, now);

        link = connectionLink.findLinkByURI(
            URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"));

        assertThat(link)
            .usingRecursiveComparison()
            .ignoringFieldsOfTypes(OffsetDateTime.class)
            .isEqualTo(expected);
        assertTrue(link.lastUpdate().until(now, ChronoUnit.SECONDS) <= 1);
        assertTrue(link.lastSeen().until(now, ChronoUnit.SECONDS) <= 1);
    }
}
