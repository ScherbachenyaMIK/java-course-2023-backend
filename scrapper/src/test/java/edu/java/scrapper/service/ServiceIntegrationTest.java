package edu.java.scrapper.service;

import edu.java.DB.jdbc.DTO.ChatDTO;
import edu.java.DB.jdbc.DTO.LinkDTO;
import edu.java.exception.NoSuchUserRegisteredException;
import edu.java.scrapper.IntegrationTest;
import edu.java.service.LinkService;
import edu.java.service.TgChatService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
public class ServiceIntegrationTest extends IntegrationTest {
    @Autowired
    private LinkService linkService;

    @Autowired
    private TgChatService tgChatService;

    @Transactional
    @Rollback
    @Test
    void IntegrationTest() throws InterruptedException {
        List<Long> chatIDs = List.of(123L, 132L, 213L, 231L, 312L, 321L);
        chatIDs.forEach(tgChatService::register);

        List<Pair<Long, URI>> linksURIs = List.of(
            Pair.of(123L, URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend")),
            Pair.of(132L, URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend")),
            Pair.of(213L, URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend")),
            Pair.of(231L, URI.create("https://api.github.com/repos/akullpp/awesome-java")),
            Pair.of(312L, URI.create("https://api.github.com/repos/akullpp/awesome-java")),
            Pair.of(321L, URI.create("https://api.github.com/repos/akullpp/awesome-java")),
            Pair.of(123L, URI.create("https://api.github.com/repos/akullpp/awesome-java")),
            Pair.of(123L, URI.create("https://api.github.com/repos/ck3g/git-best-practices")),
            Pair.of(321L, URI.create("https://api.github.com/repos/ck3g/git-best-practices")),
            Pair.of(231L, URI.create("https://api.github.com/repos/ck3g/git-best-practices")),
            Pair.of(123L, URI.create("https://api.github.com/repos/ikatyang/emoji-cheat-sheet")),
            Pair.of(213L, URI.create("https://api.github.com/repos/enhorse/java-interview"))
        );
        linksURIs.forEach(item -> linkService.add(item.getLeft(), item.getRight()));

        Thread.sleep(61000);

        List<LinkDTO> linksForUpdate = linkService.listAllByFilter();

        assertThat(linksForUpdate
            .stream()
            .map(LinkDTO::url)
            .collect(Collectors.toList()))
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(linksURIs
                .stream()
                .map(Pair::getRight)
                .collect(Collectors.toSet())
                .stream()
                .toList());

        linkService.updateLink(
            linksForUpdate.get(2).id(),
            OffsetDateTime.now(),
            OffsetDateTime.now().plusHours(1)
        );

        List<LinkDTO> linksForUpdateAfterUpdate = linkService.listAllByFilter();

        assertThat(linksForUpdateAfterUpdate.size()).isEqualTo(linksForUpdate.size() - 1);
        assertThat(linksForUpdateAfterUpdate)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(linksForUpdate
                .stream()
                .filter(item -> item.id() != linksForUpdate.get(2).id())
                .collect(Collectors.toList()));

        LinkDTO linkForSearch = linksForUpdate
            .stream()
            .filter(item -> item.url().toString().equals("https://api.github.com/repos/akullpp/awesome-java"))
            .toList()
            .getFirst();

        List<ChatDTO> chatsForLink = linkService.listChatsForLink(linkForSearch.id());

        assertThat(chatsForLink
            .stream()
            .map(ChatDTO::chatId)
            .toList())
            .contains(231L, 312L, 321L, 123L);

        List<LinkDTO> linksForChat = linkService.listAll(123L);

        assertThat(linksForChat
            .stream()
            .map(LinkDTO::url)
            .toList())
            .contains(
                URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend"),
                URI.create("https://api.github.com/repos/akullpp/awesome-java"),
                URI.create("https://api.github.com/repos/ck3g/git-best-practices"),
                URI.create("https://api.github.com/repos/ikatyang/emoji-cheat-sheet")
            );

        LinkDTO removedLink = linkService.remove(
            123L,
            URI.create("https://api.github.com/repos/akullpp/awesome-java")
        );

        assertThat(removedLink).isEqualTo(linkForSearch);

        List<ChatDTO> chatsForLinkAfterDeletion = linkService.listChatsForLink(linkForSearch.id());

        assertThat(chatsForLinkAfterDeletion.size()).isEqualTo(chatsForLink.size() - 1);
        assertThat(chatsForLinkAfterDeletion)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(chatsForLink
                .stream()
                .filter(item -> item.chatId() != 123L)
                .collect(Collectors.toList()));

        List<LinkDTO> linksForChatAfterDeletion = linkService.listAll(123L);

        assertThat(linksForChatAfterDeletion.size()).isEqualTo(linksForChat.size() - 1);
        assertThat(linksForChatAfterDeletion)
            .usingRecursiveComparison()
            .ignoringCollectionOrder()
            .isEqualTo(linksForChat
                .stream()
                .filter(item -> !Objects.equals(item.id(), linkForSearch.id()))
                .collect(Collectors.toList()));

        tgChatService.unregister(123L);

        assertThatExceptionOfType(NoSuchUserRegisteredException.class)
            .isThrownBy(() -> linkService.listAll(123L));

        linksForChatAfterDeletion
            .forEach(item -> assertThat(linkService.listChatsForLink(item.id())
                .stream()
                .map(ChatDTO::chatId)
                .toList()
                .contains(123L)).isFalse());
    }
}
