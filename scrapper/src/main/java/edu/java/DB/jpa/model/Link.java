package edu.java.DB.jpa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "link")
@Getter
@Setter
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String link;

    @NotNull
    @Column(name = "last_update")
    private Timestamp lastUpdate;

    @NotNull
    @Column(name = "last_seen", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp lastSeen = Timestamp.from(Instant.now());

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "chat_link",
               joinColumns = { @JoinColumn(name = "link_id")},
               inverseJoinColumns = {@JoinColumn (name = "chat_id")})
    private Set<Chat> chats = new HashSet<Chat>();

    public void addChat(Chat chat) {
        chats.add(chat);
    }

    public void removeChat(Chat chat) {
        chats.remove(chat);
    }
}
