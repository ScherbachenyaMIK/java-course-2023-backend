package edu.java.DB.jpa.repository;

import edu.java.DB.jpa.model.Chat;
import edu.java.DB.jpa.model.Link;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @NotNull
    Chat save(@NotNull Chat chat);

    void removeByChatId(@NotNull Long chatId);

    @NotNull
    List<Chat> findAll();

    @Query(value = """
                SELECT c.id
                FROM Chat c
                WHERE c.chatId = :chatId
                """)
    Long findIdByChatId(Long chatId);

    Chat findChatByChatId(Long chatId);

    @Query (value = """
                SELECT l
                FROM Chat c
                JOIN c.links l
                WHERE c.chatId = :chatId
                """)
    List<Link> findAllLinksForChat(@Param("chatId") Long chatId);
}
