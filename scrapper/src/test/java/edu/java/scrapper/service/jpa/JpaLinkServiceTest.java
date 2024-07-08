package edu.java.scrapper.service.jpa;

import edu.java.DB.jdbc.DTO.ChatDTO;
import edu.java.DB.jdbc.DTO.LinkDTO;
import edu.java.DB.jpa.model.Chat;
import edu.java.DB.jpa.model.Link;
import edu.java.DB.jpa.repository.ChatRepository;
import edu.java.DB.jpa.repository.LinkRepository;
import edu.java.exception.LinkNotFoundException;
import edu.java.exception.NoSuchUserRegisteredException;
import edu.java.responseDTO.GitHubResponse;
import edu.java.responseDTO.StackOverflowResponse;
import edu.java.service.jpa.JpaLinkService;
import edu.java.util.LinkEntityDTOConverter;
import edu.java.web.GitHubClient;
import edu.java.web.StackOverflowClient;
import java.net.URI;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JpaLinkServiceTest {
    @Mock
    LinkRepository linkRepository;

    @Mock
    ChatRepository chatRepository;

    @Mock
    private GitHubClient gitHubClient;

    @Mock
    private StackOverflowClient stackOverflowClient;

    @InjectMocks
    private JpaLinkService jpaLinkService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void add_LinkGithubNotInDatabase_AddedSuccessfully() {
        long tgChatId = 123L;
        URI url = URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend");
        Chat existingChat = new Chat();
        existingChat.setChatId(1L);
        existingChat.setChatId(123L);
        OffsetDateTime now = OffsetDateTime.now();
        Link existinglink = new Link();
        existinglink.setLink(url.toString());
        existinglink.setLastUpdate(Timestamp.from(now.toInstant()));
        when(chatRepository.findChatByChatId(tgChatId)).thenReturn(existingChat);
        when(linkRepository.findLinkByLink(url.toString())).thenReturn(null, existinglink);
        when(gitHubClient.getResponse(anyString(), anyString()))
            .thenReturn(new GitHubResponse("1", now));

        jpaLinkService.add(tgChatId, url);

        verify(linkRepository, times(1)).save(any());
    }

    @Test
    void add_LinkStackOverflowNotInDatabase_AddedSuccessfully() {
        long tgChatId = 123L;
        URI url = URI.create("https://api.stackexchange.com/2.2/questions" +
            "/12345?order=desc&sort=activity&site=stackoverflow");
        Chat existingChat = new Chat();
        existingChat.setChatId(1L);
        existingChat.setChatId(123L);
        OffsetDateTime now = OffsetDateTime.now();
        Link existinglink = new Link();
        existinglink.setLink(url.toString());
        existinglink.setLastUpdate(Timestamp.from(now.toInstant()));
        when(chatRepository.findChatByChatId(tgChatId)).thenReturn(existingChat);
        when(linkRepository.findLinkByLink(url.toString())).thenReturn(null, existinglink);
        when(stackOverflowClient.getResponse(anyString()))
            .thenReturn(new StackOverflowResponse("1", now));

        jpaLinkService.add(tgChatId, url);

        verify(linkRepository, times(1)).save(any());
    }

    @Test
    void add_LinkInDatabase_ReturnExistingLink() {
        long tgChatId = 123L;
        URI url = URI.create("https://api.stackexchange.com/2.2/questions" +
            "/12345?order=desc&sort=activity&site=stackoverflow");
        Chat existingChat = new Chat();
        existingChat.setChatId(1L);
        existingChat.setChatId(123L);
        OffsetDateTime now = OffsetDateTime.now();
        Link existinglink = new Link();
        existinglink.setLink(url.toString());
        existinglink.setLastUpdate(Timestamp.from(now.toInstant()));
        when(chatRepository.findChatByChatId(tgChatId)).thenReturn(existingChat);
        when(linkRepository.findLinkByLink(url.toString())).thenReturn(existinglink);

        jpaLinkService.add(tgChatId, url);

        verify(linkRepository, never()).save(any());
    }

    @Test
    void add_UserNotRegistered_ExceptionThrown() {
        long tgChatId = 123L;
        URI url = URI.create("https://github.com/user/repo");
        when(chatRepository.findChatByChatId(tgChatId)).thenReturn(null);

        assertThrows(NoSuchUserRegisteredException.class, () -> jpaLinkService.add(tgChatId, url));

        verify(linkRepository, never()).save(any());
    }

    @Test
    void remove_LinkInDatabase_RemovedSuccessfully() {
        long tgChatId = 123L;
        URI url = URI.create("https://github.com/user/repo");
        Chat existingChat = new Chat();
        existingChat.setChatId(1L);
        existingChat.setChatId(1234567L);
        LinkDTO existingLink = new LinkDTO(1L, url, OffsetDateTime.now(), OffsetDateTime.now());
        when(chatRepository.findChatByChatId(tgChatId)).thenReturn(existingChat);
        when(linkRepository.findLinkByLink(url.toString())).thenReturn(LinkEntityDTOConverter.convert(existingLink));

        LinkDTO removedLink = jpaLinkService.remove(tgChatId, url);

        assertNotNull(removedLink);
        assertEquals(existingLink, removedLink);
    }

    @Test
    void remove_LinkNotFoundException() {
        long tgChatId = 123L;
        URI url = URI.create("https://github.com/user/repo");
        Chat existingChat = new Chat();
        existingChat.setChatId(1L);
        existingChat.setChatId(1234567L);
        when(chatRepository.findChatByChatId(tgChatId)).thenReturn(existingChat);
        when(linkRepository.findLinkByLink(url.toString())).thenReturn(null);

        assertThrows(LinkNotFoundException.class, () -> jpaLinkService.remove(tgChatId, url));
    }

    @Test
    void remove_NoSuchUserRegisteredException() {
        long tgChatId = 123L;
        URI url = URI.create("https://github.com/user/repo");
        when(chatRepository.findChatByChatId(tgChatId)).thenReturn(null);

        assertThrows(NoSuchUserRegisteredException.class, () -> jpaLinkService.remove(tgChatId, url));
    }

    @Test
    void listAll_ReturnsListOfLinks() {
        long tgChatId = 1L;
        OffsetDateTime now = OffsetDateTime.now();
        Link link1 = new Link();
        link1.setId(1L);
        link1.setLink("https://link1.com");
        link1.setLastUpdate(Timestamp.from(now.toInstant()));
        link1.setLastSeen(Timestamp.from(now.toInstant()));
        Link link2 = new Link();
        link2.setId(2L);
        link2.setLink("https://link2.com");
        link2.setLastUpdate(Timestamp.from(now.toInstant()));
        link2.setLastSeen(Timestamp.from(now.toInstant()));
        List<Link> linkList = List.of(
            link1,
            link2
        );
        List<LinkDTO> linkDTOSList = List.of(
            new LinkDTO(1L, URI.create("https://link1.com"), now, now),
            new LinkDTO(2L, URI.create("https://link2.com"), now, now)
        );
        when(chatRepository.findIdByChatId(tgChatId)).thenReturn(1L);
        when(chatRepository.findAllLinksForChat(1L)).thenReturn(linkList);

        List<LinkDTO> returnedLinks = jpaLinkService.listAll(tgChatId);

        assertNotNull(returnedLinks);
        assertEquals(linkList.size(), returnedLinks.size());
        assertEquals(linkDTOSList, returnedLinks);
    }

    @Test
    void listAll_NoSuchUserRegisteredException() {
        long tgChatId = 123L;

        when(chatRepository.findIdByChatId(tgChatId)).thenReturn(null);


        assertThrows(NoSuchUserRegisteredException.class, () -> jpaLinkService.listAll(tgChatId));
        verify(chatRepository, never()).findAllLinksForChat(anyLong());
    }

    @Test
    void listAllByFilter_ReturnsListOfLinks() {
        long tgChatId = 1L;
        OffsetDateTime now = OffsetDateTime.now();
        Link link1 = new Link();
        link1.setId(1L);
        link1.setLink("https://link1.com");
        link1.setLastUpdate(Timestamp.from(now.toInstant()));
        link1.setLastSeen(Timestamp.from(now.toInstant()));
        Link link2 = new Link();
        link2.setId(2L);
        link2.setLink("https://link2.com");
        link2.setLastUpdate(Timestamp.from(now.toInstant()));
        link2.setLastSeen(Timestamp.from(now.toInstant()));
        List<Link> linkList = List.of(
            link1,
            link2
        );
        List<LinkDTO> linkDTOSList = List.of(
            new LinkDTO(1L, URI.create("https://link1.com"), now, now),
            new LinkDTO(2L, URI.create("https://link2.com"), now, now)
        );
        when(linkRepository.findAllLinksWithFilter()).thenReturn(linkList);

        List<LinkDTO> returnedLinks = jpaLinkService.listAllByFilter();

        assertNotNull(returnedLinks);
        assertEquals(linkList.size(), returnedLinks.size());
        assertEquals(linkDTOSList, returnedLinks);
    }

    @Test
    void listChatsForLink_ReturnsListOfChats() {
        Long linkId = 123L;
        Chat chat1 = new Chat();
        chat1.setId(1L);
        chat1.setChatId(1L);
        Chat chat2 = new Chat();
        chat2.setId(2L);
        chat2.setChatId(2L);
        List<Chat> chatList = List.of(
            chat1,
            chat2
        );
        List<ChatDTO> chatDTOSList = List.of(
            new ChatDTO(1L, 1L),
            new ChatDTO(2L, 2L)
        );
        when(linkRepository.findAllChatsForLink(linkId)).thenReturn(chatList);

        List<ChatDTO> returnedChats = jpaLinkService.listChatsForLink(linkId);

        assertNotNull(returnedChats);
        assertEquals(chatList.size(), returnedChats.size());
        assertEquals(chatDTOSList, returnedChats);
    }

    @Test
    void updateLink_UpdatesLinkSuccessfully() {
        Long linkId = 123L;
        OffsetDateTime lastUpdate = OffsetDateTime.now();
        OffsetDateTime lastSeen = OffsetDateTime.now();
        Link link = new Link();
        link.setId(linkId);
        link.setLastUpdate(Timestamp.from(lastUpdate.toInstant()));
        link.setLastSeen(Timestamp.from(lastSeen.toInstant()));

        when(linkRepository.findLinkById(linkId)).thenReturn(link);

        jpaLinkService.updateLink(linkId, lastUpdate, lastSeen);

        verify(linkRepository, times(1)).saveAndFlush(link);
    }
}
