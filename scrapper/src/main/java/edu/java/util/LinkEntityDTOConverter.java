package edu.java.util;

import edu.java.DB.jdbc.DTO.LinkDTO;
import edu.java.DB.jpa.model.Link;
import java.net.URI;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@SuppressWarnings("HideUtilityClassConstructor")
public class LinkEntityDTOConverter {
    public static LinkDTO convert(Link link) {
        return new LinkDTO(
            link.getId(),
            URI.create(link.getLink()),
            OffsetDateTime.ofInstant(link.getLastUpdate().toInstant(), ZoneOffset.systemDefault()),
            OffsetDateTime.ofInstant(link.getLastSeen().toInstant(), ZoneOffset.systemDefault())
        );
    }

    public static Link convert(LinkDTO linkDTO) {
        Link link = new Link();
        link.setId(linkDTO.id());
        link.setLink(linkDTO.url().toString());
        link.setLastUpdate(Timestamp.from(linkDTO.lastUpdate().toInstant()));
        link.setLastSeen(Timestamp.from(linkDTO.lastSeen().toInstant()));
        return link;
    }
}
