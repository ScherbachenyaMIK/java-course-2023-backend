package edu.java.scrapper.DB.jpa;

import edu.java.DB.jpa.model.Chat;
import edu.java.DB.jpa.model.Link;
import edu.java.DB.jpa.repository.ChatRepository;
import edu.java.DB.jpa.repository.LinkRepository;
import edu.java.scrapper.IntegrationTest;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class LinkRepositoryTest extends IntegrationTest {
    @Autowired
    LinkRepository linkRepository;

    @Autowired
    ChatRepository chatRepository;

    @Transactional
    @Rollback
    @Test
    void save() {
        Link link1 = new Link();
        Link link2 = new Link();

        link1.setLink(
            "https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend");
        link1.setLastUpdate(Timestamp.from(
            OffsetDateTime.of(
            2024,
            3,
            15,
            18,
            0,
            0,
            0,
            ZoneOffset.UTC
        ).toInstant()));
        link2.setLink(
            "https://api.stackexchange.com/2.2/questions" +
                "/78110387?order=desc&sort=activity&site=stackoverflow");
        link2.setLastUpdate(Timestamp.from(
            OffsetDateTime.of(
                2024,
                2,
                16,
                14,
                0,
                0,
                0,
                ZoneOffset.UTC
            ).toInstant()));

        linkRepository.save(link1);
        linkRepository.save(link2);
    }

    @Transactional
    @Rollback
    @Test
    void findAll() {
        Link link1 = new Link();
        Link link2 = new Link();

        link1.setLink(
            "https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend");
        link1.setLastUpdate(Timestamp.from(
            OffsetDateTime.of(
                2024,
                3,
                15,
                18,
                0,
                0,
                0,
                ZoneOffset.UTC
            ).toInstant()));
        link2.setLink(
            "https://api.stackexchange.com/2.2/questions" +
                "/78110387?order=desc&sort=activity&site=stackoverflow");
        link2.setLastUpdate(Timestamp.from(
            OffsetDateTime.of(
                2024,
                2,
                16,
                14,
                0,
                0,
                0,
                ZoneOffset.UTC
            ).toInstant()));

        linkRepository.save(link1);
        linkRepository.save(link2);

        List<Link> expected = new ArrayList<>();
        expected.add(link1);
        expected.add(link2);

        List<Link> result = linkRepository.findAll();

        assertThat(result.size()).isEqualTo(expected.size());
        assertThat(result.get(0))
            .usingRecursiveComparison()
            .ignoringFields("id", "lastSeen", "chats")
            .isEqualTo(expected.get(0));
        assertThat(result.get(1))
            .usingRecursiveComparison()
            .ignoringFields("id", "lastSeen", "chats")
            .isEqualTo(expected.get(1));
    }

    @Transactional
    @Rollback
    @Test
    void removeByLink() {
        Link link1 = new Link();
        Link link2 = new Link();

        link1.setLink(
            "https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend");
        link1.setLastUpdate(Timestamp.from(
            OffsetDateTime.of(
                2024,
                3,
                15,
                18,
                0,
                0,
                0,
                ZoneOffset.UTC
            ).toInstant()));
        link2.setLink(
            "https://api.stackexchange.com/2.2/questions" +
                "/78110387?order=desc&sort=activity&site=stackoverflow");
        link2.setLastUpdate(Timestamp.from(
            OffsetDateTime.of(
                2024,
                2,
                16,
                14,
                0,
                0,
                0,
                ZoneOffset.UTC
            ).toInstant()));

        linkRepository.save(link1);
        linkRepository.save(link2);

        List<Link> expected = new ArrayList<>();
        expected.add(link2);

        linkRepository.removeByLink(
            "https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"
        );

        List<Link> result = linkRepository.findAll();

        assertThat(result.size()).isEqualTo(expected.size());
        assertThat(result.get(0))
            .usingRecursiveComparison()
            .ignoringFields("id", "lastSeen", "chats")
            .isEqualTo(expected.get(0));
    }

    @Transactional
    @Rollback
    @Test
    void findLinkById() {
        Link link1 = new Link();

        link1.setLink(
            "https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend");
        link1.setLastUpdate(Timestamp.from(
            OffsetDateTime.of(
                2024,
                3,
                15,
                18,
                0,
                0,
                0,
                ZoneOffset.UTC
            ).toInstant()));

        linkRepository.save(link1);

        Link foundLink = linkRepository
            .findLinkByLink(
                "https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"
            );
        Link result_link = linkRepository
            .findLinkById(
                foundLink.getId()
            );

        assertThat(result_link).isEqualTo(foundLink);
    }

    @Transactional
    @Rollback
    @Test
    void findLinkByLink() {
        Link link1 = new Link();

        link1.setLink(
            "https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend");
        link1.setLastUpdate(Timestamp.from(
            OffsetDateTime.of(
                2024,
                3,
                15,
                18,
                0,
                0,
                0,
                ZoneOffset.UTC
            ).toInstant()));

        linkRepository.save(link1);

        Link result_link = linkRepository
            .findLinkByLink(
                "https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"
            );

        assertThat(result_link)
            .usingRecursiveComparison()
            .ignoringFields("id", "lastSeen", "chats")
            .isEqualTo(link1);

        linkRepository
            .removeByLink("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"
            );

        assertThat(
            linkRepository
                .findLinkByLink(
                    "https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"
                )).isNull();
    }

    @Transactional
    @Rollback
    @Test
    void findAllLinksWithFilter() {
        Link link1 = new Link();
        Link link2 = new Link();
        Link link3 = new Link();

        link1.setLink("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend");
        link2.setLink("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend2");
        link3.setLink("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend3");

        link1.setLastUpdate(Timestamp.from(OffsetDateTime.now().toInstant()));
        link2.setLastUpdate(Timestamp.from(OffsetDateTime.now().toInstant()));
        link3.setLastUpdate(Timestamp.from(OffsetDateTime.now().toInstant()));

        link1.setLastSeen(Timestamp.from(OffsetDateTime.now().plusHours(1).toInstant()));
        link2.setLastSeen(Timestamp.from(OffsetDateTime.now().minusMinutes(3).toInstant()));
        link3.setLastSeen(Timestamp.from(OffsetDateTime.now().minusMinutes(4).toInstant()));

        linkRepository.save(link1);
        linkRepository.save(link2);
        linkRepository.save(link3);

        Link firstLink = linkRepository.findLinkByLink(
            "https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend2");
        Link secondLink = linkRepository.findLinkByLink(
            "https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend3");
        List<Link> expected = new ArrayList<>();
        expected.add(firstLink);
        expected.add(secondLink);

        List<Link> result = linkRepository.findAllLinksWithFilter();

        assertThat(result).isEqualTo(expected);
    }

    @Transactional
    @Rollback
    @Test
    void findAllChatsForLink() {
        Link link1 = new Link();
        Link link2 = new Link();
        Link link3 = new Link();

        link1.setLink("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend");
        link1.setLastUpdate(Timestamp.from(Instant.now()));
        link2.setLink("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend2");
        link2.setLastUpdate(Timestamp.from(Instant.now()
            .minus(1, ChronoUnit.MINUTES)));
        link3.setLink("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend3");
        link3.setLastUpdate(Timestamp.from(Instant.now()
            .minus(5, ChronoUnit.MINUTES)));

        linkRepository.save(link1);
        linkRepository.save(link2);
        linkRepository.save(link3);

        Chat chat1 = new Chat();
        Chat chat2 = new Chat();

        chat1.setChatId(1234567L);
        chat2.setChatId(7654321L);

        chatRepository.save(chat1);
        chatRepository.save(chat2);

        link1 = linkRepository.findLinkByLink(
            "https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend");
        link2 = linkRepository.findLinkByLink(
            "https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend2");
        link3 = linkRepository.findLinkByLink(
            "https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend3");

        link1.addChat(chat1);
        link2.addChat(chat1);
        link2.addChat(chat2);
        link3.addChat(chat2);
        linkRepository.flush();

        List<Chat> result = linkRepository.findAllChatsForLink(link3.getId());

        assertThat(result.contains(chat1)).isFalse();
        assertThat(result.contains(chat2)).isTrue();
    }
}
