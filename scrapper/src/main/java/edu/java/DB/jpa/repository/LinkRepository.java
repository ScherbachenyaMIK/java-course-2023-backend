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
public interface LinkRepository extends JpaRepository<Link, Long> {
    @NotNull
    Link save(@NotNull Link link);

    void removeByLink(@NotNull String link);

    @NotNull
    List<Link> findAll();

    Link findLinkById(Long id);

    Link findLinkByLink(String link);

    @Query(value = """
            SELECT *
            FROM link
            WHERE last_seen < NOW() - INTERVAL '1' MINUTE
            """, nativeQuery = true)
    List<Link> findAllLinksWithFilter();

    @Query (value = """
            SELECT c
            FROM Link l
            JOIN l.chats c
            WHERE l.id = :linkId
            """)
    List<Chat> findAllChatsForLink(@Param("linkId") Long linkId);
}
