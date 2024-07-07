package edu.java.DB.jdbc.DAO;

import edu.java.DB.jdbc.DTO.ChatDTO;
import edu.java.DB.jdbc.DTO.LinkDTO;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ChatDAO {

    private final JdbcTemplate jdbcTemplate;

    public ChatDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addChat(@NotNull long chatId) {
        jdbcTemplate.update("INSERT INTO Chat (chat_id) VALUES (?)", chatId);
    }

    public void removeChat(@NotNull long chatId) {
        jdbcTemplate.update("DELETE FROM Chat WHERE chat_id = ?", chatId);
    }

    public List<ChatDTO> findAllChats() {
        return jdbcTemplate.query("SELECT * FROM Chat",
            (rs, rowNum) -> new ChatDTO(
                rs.getLong("id"),
                rs.getLong("chat_id")));
    }

    public Long findChatId(Long chatId) {
        List<Long> foundChatId = jdbcTemplate.query(
            "SELECT id FROM Chat WHERE chat_id = (?)",
            new Object[]{chatId},
            (rs, rowNum) -> rs.getLong("id"));
        return foundChatId.isEmpty() ? -1 : foundChatId.getFirst();
    }

    public List<LinkDTO> findAllLinksForChat(Long chatId) {
        return jdbcTemplate.query("""
                                    SELECT l.id, l.link, l.last_update, l.last_seen
                                    FROM Chat c
                                    JOIN Chat_Link cl ON c.id = cl.chat_id
                                    JOIN Link l ON cl.link_id = l.id
                                    WHERE c.chat_id = (?);""", new Object[]{chatId},
            (rs, rowNum) -> {
                return new LinkDTO(
                    rs.getLong("id"),
                    URI.create(rs.getString("link")),
                    OffsetDateTime.ofInstant(
                        rs.getTimestamp("last_update").toInstant(),
                        ZoneOffset.ofHours(0)
                    ),
                    OffsetDateTime.ofInstant(
                        rs.getTimestamp("last_seen").toInstant(),
                        ZoneOffset.ofHours(0)
                    ));
            }
        );
    }
}
