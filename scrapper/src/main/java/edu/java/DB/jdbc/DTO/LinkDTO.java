package edu.java.DB.jdbc.DTO;

import java.net.URI;
import java.time.OffsetDateTime;

public record LinkDTO(
    Long id,
    URI url,
    OffsetDateTime lastUpdate,
    OffsetDateTime lastSeen
) {
}
