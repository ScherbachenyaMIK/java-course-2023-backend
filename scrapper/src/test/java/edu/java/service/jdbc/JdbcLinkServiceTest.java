package edu.java.service.jdbc;

import edu.java.DB.DAO.ChatDAO;
import edu.java.DB.DAO.ChatLinkDAO;
import edu.java.DB.DAO.LinkDAO;
import edu.java.DB.DTO.ChatDTO;
import edu.java.DB.DTO.LinkDTO;
import edu.java.exception.LinkNotFoundException;
import edu.java.exception.NoSuchUserRegisteredException;
import edu.java.responseDTO.GitHubResponse;
import edu.java.responseDTO.StackOverflowResponse;
import edu.java.scrapper.IntegrationTest;
import edu.java.web.GitHubClient;
import edu.java.web.StackOverflowClient;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JdbcLinkServiceTest extends IntegrationTest {

    @Mock
    private LinkDAO linkDAO;

    @Mock
    private ChatDAO chatDAO;

    @Mock
    private ChatLinkDAO chatLinkDAO;

    @Mock
    private GitHubClient gitHubClient;

    @Mock
    private StackOverflowClient stackOverflowClient;

    @InjectMocks
    private JdbcLinkService jdbcLinkService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void add_LinkGithubNotInDatabase_AddedSuccessfully() {
        long tgChatId = 123L;
        URI url = URI.create("https://api.github.com/repos/ScherbachenyaMIK/java-course-2023-backend");
        LinkDTO notExistingLink = new LinkDTO(-1L, url, OffsetDateTime.now(), OffsetDateTime.now());
        when(chatDAO.findChatId(tgChatId)).thenReturn(1L);
        when(linkDAO.findLinkByURI(url)).thenReturn(notExistingLink);
        when(gitHubClient.getResponse(anyString(), anyString()))
            .thenReturn(new GitHubResponse("1", notExistingLink.lastUpdate()));

        LinkDTO addedLink = jdbcLinkService.add(tgChatId, url);

        assertNotNull(addedLink);
        assertThat(notExistingLink).isEqualTo(notExistingLink);

        verify(linkDAO, times(1)).addLink(url, notExistingLink.lastUpdate());
    }

    @Test
    void add_LinkStackOverflowNotInDatabase_AddedSuccessfully() {
        long tgChatId = 123L;
        URI url = URI.create("https://api.stackexchange.com/2.2/questions" +
            "/12345?order=desc&sort=activity&site=stackoverflow");
        LinkDTO notExistingLink = new LinkDTO(-1L, url, OffsetDateTime.now(), OffsetDateTime.now());
        when(chatDAO.findChatId(tgChatId)).thenReturn(1L);
        when(linkDAO.findLinkByURI(url)).thenReturn(notExistingLink);
        when(stackOverflowClient.getResponse(anyString()))
            .thenReturn(new StackOverflowResponse("1", notExistingLink.lastUpdate()));

        LinkDTO addedLink = jdbcLinkService.add(tgChatId, url);

        assertNotNull(addedLink);
        assertThat(notExistingLink).isEqualTo(notExistingLink);

        verify(linkDAO, times(1)).addLink(url, notExistingLink.lastUpdate());
    }

    @Test
    void add_LinkInDatabase_ReturnExistingLink() {
        long tgChatId = 123L;
        URI url = URI.create("https://github.com/user/repo");
        LinkDTO existingLink = new LinkDTO(1L, url, OffsetDateTime.now(), OffsetDateTime.now());
        when(chatDAO.findChatId(tgChatId)).thenReturn(1L);
        when(linkDAO.findLinkByURI(url)).thenReturn(existingLink);

        LinkDTO addedLink = jdbcLinkService.add(tgChatId, url);

        assertNotNull(addedLink);
        assertEquals(existingLink, addedLink);

        verify(linkDAO, never()).addLink(any(), any());
    }

    @Test
    void add_UserNotRegistered_ExceptionThrown() {
        long tgChatId = 123L;
        URI url = URI.create("https://github.com/user/repo");
        when(chatDAO.findChatId(tgChatId)).thenReturn(-1L);

        assertThrows(NoSuchUserRegisteredException.class, () -> jdbcLinkService.add(tgChatId, url));

        verify(linkDAO, never()).addLink(any(), any());
    }

    @Test
    void remove_LinkInDatabase_RemovedSuccessfully() {
        long tgChatId = 123L;
        URI url = URI.create("https://github.com/user/repo");
        LinkDTO existingLink = new LinkDTO(1L, url, OffsetDateTime.now(), OffsetDateTime.now());
        when(chatDAO.findChatId(tgChatId)).thenReturn(1L);
        when(linkDAO.findLinkByURI(url)).thenReturn(existingLink);

        LinkDTO removedLink = jdbcLinkService.remove(tgChatId, url);

        assertNotNull(removedLink);
        assertEquals(existingLink, removedLink);
        verify(chatLinkDAO, times(1)).removeChatLink(1L, existingLink.id());
    }

    @Test
    void remove_LinkNotFoundException() {
        long tgChatId = 123L;
        URI url = URI.create("https://github.com/user/repo");
        when(chatDAO.findChatId(tgChatId)).thenReturn(1L);
        when(linkDAO.findLinkByURI(url)).thenReturn(new LinkDTO(-1L, url, OffsetDateTime.now(), OffsetDateTime.now()));

        assertThrows(LinkNotFoundException.class, () -> jdbcLinkService.remove(tgChatId, url));
        verify(chatLinkDAO, never()).removeChatLink(anyLong(), anyLong());
    }

    @Test
    void remove_NoSuchUserRegisteredException() {
        long tgChatId = 123L;
        URI url = URI.create("https://github.com/user/repo");
        when(chatDAO.findChatId(tgChatId)).thenReturn(-1L);

        assertThrows(NoSuchUserRegisteredException.class, () -> jdbcLinkService.remove(tgChatId, url));
        verify(chatLinkDAO, never()).removeChatLink(anyLong(), anyLong());
    }

    @Test
    void listAll_ReturnsListOfLinks() {
        long tgChatId = 1L;
        List<LinkDTO> linkList = List.of(
            new LinkDTO(1L, URI.create("https://link1.com"), OffsetDateTime.now(), OffsetDateTime.now()),
            new LinkDTO(2L, URI.create("https://link2.com"), OffsetDateTime.now(), OffsetDateTime.now())
        );
        when(chatDAO.findChatId(tgChatId)).thenReturn(1L);
        when(chatDAO.findAllLinksForChat(1L)).thenReturn(linkList);

        List<LinkDTO> returnedLinks = jdbcLinkService.listAll(tgChatId);

        assertNotNull(returnedLinks);
        assertEquals(linkList.size(), returnedLinks.size());
        assertEquals(linkList, returnedLinks);
    }

    @Test
    void listAll_NoSuchUserRegisteredException() {
        long tgChatId = 123L;

        when(chatDAO.findChatId(tgChatId)).thenReturn(-1L);


        assertThrows(NoSuchUserRegisteredException.class, () -> jdbcLinkService.listAll(tgChatId));
        verify(chatDAO, never()).findAllLinksForChat(anyLong());
    }

    @Test
    void listAllByFilter_ReturnsListOfLinks() {
        List<LinkDTO> linkList = List.of(
            new LinkDTO(1L, URI.create("https://link1.com"), OffsetDateTime.now(), OffsetDateTime.now()),
            new LinkDTO(2L, URI.create("https://link2.com"), OffsetDateTime.now(), OffsetDateTime.now())
        );
        when(linkDAO.findAllLinksWithFilter()).thenReturn(linkList);

        List<LinkDTO> returnedLinks = jdbcLinkService.listAllByFilter();

        assertNotNull(returnedLinks);
        assertEquals(linkList.size(), returnedLinks.size());
        assertEquals(linkList, returnedLinks);
    }

    @Test
    void listChatsForLink_ReturnsListOfChats() {
        Long linkId = 123L;
        List<ChatDTO> chatList = List.of(
            new ChatDTO(1L, 1L),
            new ChatDTO(2L, 2L)
        );
        when(linkDAO.findAllChatsForLink(linkId)).thenReturn(chatList);

        List<ChatDTO> returnedChats = jdbcLinkService.listChatsForLink(linkId);

        assertNotNull(returnedChats);
        assertEquals(chatList.size(), returnedChats.size());
        assertEquals(chatList, returnedChats);
    }

    @Test
    void updateLink_UpdatesLinkSuccessfully() {
        Long linkId = 123L;
        OffsetDateTime lastUpdate = OffsetDateTime.now();
        OffsetDateTime lastSeen = OffsetDateTime.now();

        jdbcLinkService.updateLink(linkId, lastUpdate, lastSeen);

        verify(linkDAO, times(1)).updateLink(linkId, lastUpdate, lastSeen);
    }
}
