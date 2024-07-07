package edu.java.DB.jdbc.DAO;

import edu.java.DB.jdbc.DTO.ChatLinkDTO;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ChatLinkDAO {

    private final JdbcTemplate jdbcTemplate;

    public ChatLinkDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addChatLink(@NotNull long chatId, @NotNull long linkId) {
        jdbcTemplate.update(
            "INSERT INTO Chat_Link (chat_id, link_id) VALUES (?, ?)",
            chatId,
            linkId);
    }

    public void removeChatLink(@NotNull long chatId, @NotNull long linkId) {
        jdbcTemplate.update(
            "DELETE FROM Chat_Link WHERE chat_id = ? AND link_id = ?",
            chatId,
            linkId);
    }

    public List<ChatLinkDTO> findAllChatLinks() {
        return jdbcTemplate.query("SELECT * FROM Chat_Link",
            (rs, rowNum) -> new ChatLinkDTO(
                rs.getLong("chat_id"),
                rs.getLong("link_id")));
    }
}
