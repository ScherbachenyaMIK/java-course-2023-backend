package edu.java.DB.jdbc.DAO;

import edu.java.DB.jdbc.DTO.ChatDTO;
import edu.java.DB.jdbc.DTO.LinkDTO;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LinkDAO {
    private final JdbcTemplate jdbcTemplate;

    public LinkDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void addLink(@NotNull URI url, @NotNull OffsetDateTime lastUpdate) {
        jdbcTemplate.update("INSERT INTO Link (link, last_update) VALUES (?, ?)", url.toString(),
            Timestamp.from(lastUpdate.toInstant()));
    }

    public void removeLink(@NotNull URI url) {
        jdbcTemplate.update("DELETE FROM Link WHERE link = ?", url.toString());
    }

    @SuppressWarnings("MultipleStringLiterals")
    public List<LinkDTO> findAllLinks() {
        return jdbcTemplate.query("SELECT * FROM Link",
            (rs, rowNum) -> {
                try {
                    return new LinkDTO(
                        rs.getLong("id"),
                        new URI(rs.getString("link")),
                        OffsetDateTime.ofInstant(
                            rs.getTimestamp("last_update").toInstant(),
                            ZoneOffset.ofHours(0)
                        ),
                        OffsetDateTime.ofInstant(
                            rs.getTimestamp("last_seen").toInstant(),
                            ZoneOffset.ofHours(0)
                        ));
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        );
    }

    public LinkDTO findLinkByURI(URI url) {
        List<LinkDTO> foundVal = jdbcTemplate.query(
            "SELECT * FROM Link WHERE link = ?",
            new Object[]{url.toString()},
            (rs, rowNum) -> {
                try {
                    return new LinkDTO(
                        rs.getLong("id"),
                        new URI(rs.getString("link")),
                        OffsetDateTime.ofInstant(
                            rs.getTimestamp("last_update").toInstant(),
                            ZoneOffset.ofHours(0)
                        ),
                        OffsetDateTime.ofInstant(
                            rs.getTimestamp("last_seen").toInstant(),
                            ZoneOffset.ofHours(0)
                        ));
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        );
        return foundVal.isEmpty()
            ? new LinkDTO(-1L, url, OffsetDateTime.now(), OffsetDateTime.now())
            : foundVal.getFirst();
    }

    public List<LinkDTO> findAllLinksWithFilter() {
        return jdbcTemplate.query("SELECT * FROM Link WHERE last_seen < NOW() - INTERVAL '1 minutes'",
            (rs, rowNum) -> {
                try {
                    return new LinkDTO(
                        rs.getLong("id"),
                        new URI(rs.getString("link")),
                        OffsetDateTime.ofInstant(
                            rs.getTimestamp("last_update").toInstant(),
                            ZoneOffset.ofHours(0)
                        ),
                        OffsetDateTime.ofInstant(
                            rs.getTimestamp("last_seen").toInstant(),
                            ZoneOffset.ofHours(0)
                        ));
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        );
    }

    public List<ChatDTO> findAllChatsForLink(Long linkId) {
        return jdbcTemplate.query("""
                                    SELECT c.id, c.chat_id
                                    FROM Link l
                                    JOIN Chat_Link cl ON l.id = cl.link_id
                                    JOIN Chat c ON cl.chat_id = c.id
                                    WHERE l.id = (?);""", new Object[]{linkId},
            (rs, rowNum) -> new ChatDTO(
                rs.getLong("id"),
                rs.getLong("chat_id"))
        );
    }

    public void updateLink(@NotNull Long linkId,
        @NotNull OffsetDateTime lastUpdate, @NotNull OffsetDateTime lastSeen) {
        jdbcTemplate.update("""
                                    UPDATE Link
                                    SET last_update = (?), last_seen = (?)
                                    WHERE id = (?)""",
            Timestamp.from(lastUpdate.toInstant()),
            Timestamp.from(lastSeen.toInstant()),
            linkId
            );
    }
}
