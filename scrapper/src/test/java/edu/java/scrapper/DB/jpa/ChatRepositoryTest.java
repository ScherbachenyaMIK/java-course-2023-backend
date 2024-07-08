package edu.java.scrapper.DB.jpa;

import edu.java.DB.jdbc.DTO.ChatDTO;
import edu.java.DB.jpa.model.Chat;
import edu.java.DB.jpa.model.Link;
import edu.java.DB.jpa.repository.ChatRepository;
import edu.java.DB.jpa.repository.LinkRepository;
import edu.java.scrapper.IntegrationTest;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ChatRepositoryTest extends IntegrationTest {
    @Autowired
    ChatRepository chatRepository;

    @Autowired
    LinkRepository linkRepository;

    @Transactional
    @Rollback
    @Test
    void save() {
        Chat chat1 = new Chat();
        Chat chat2 = new Chat();

        chat1.setChatId(1234567L);
        chat2.setChatId(7654321L);

        chatRepository.save(chat1);
        chatRepository.save(chat2);
    }

    @Transactional
    @Rollback
    @Test
    void findAll() {
        Chat chat1 = new Chat();
        Chat chat2 = new Chat();

        chat1.setChatId(1234567L);
        chat2.setChatId(7654321L);

        chatRepository.save(chat1);
        chatRepository.save(chat2);

        List<ChatDTO> expected = new ArrayList<>();
        expected.add(new ChatDTO(1L, 1234567L));
        expected.add(new ChatDTO(2L, 7654321L));

        List<Chat> result = chatRepository.findAll();

        assertThat(result.size()).isEqualTo(expected.size());
        assertThat(result.get(0).getChatId())
            .isEqualTo(expected.get(0).chatId());
        assertThat(result.get(1).getChatId())
            .isEqualTo(expected.get(1).chatId());
    }

    @Transactional
    @Rollback
    @Test
    void removeByChatId() {
        Chat chat1 = new Chat();
        Chat chat2 = new Chat();

        chat1.setChatId(1234567L);
        chat2.setChatId(7654321L);

        chatRepository.save(chat1);
        chatRepository.save(chat2);

        chatRepository.removeByChatId(1234567L);

        List<ChatDTO> expected = new ArrayList<>();
        expected.add(new ChatDTO(2L, 7654321L));

        List<Chat> result = chatRepository.findAll();

        assertThat(result.size()).isEqualTo(expected.size());
        assertThat(result.get(0).getChatId())
            .isEqualTo(expected.get(0).chatId());
    }

    @Transactional
    @Rollback
    @Test
    void findIdByChatId() {
        Chat chat = new Chat();

        chat.setChatId(1234567L);

        chatRepository.save(chat);

        Long result = chatRepository.findIdByChatId(1234567L);

        assertNotNull(result);
    }

    @Transactional
    @Rollback
    @Test
    void findChatByChatId() {
        Chat chat = new Chat();

        chat.setChatId(1234567L);

        chatRepository.save(chat);

        Chat result = chatRepository.findChatByChatId(1234567L);

        assertNotNull(result);
    }

    @Transactional
    @Rollback
    @Test
    void findAllLinksForChat() {
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

        List<Link> result = chatRepository.findAllLinksForChat(chat1.getChatId());

        assertThat(result.contains(link1)).isTrue();
        assertThat(result.contains(link2)).isTrue();
        assertThat(result.contains(link3)).isFalse();
    }
}
