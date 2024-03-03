package edu.java.bot.model.requestDTO;

import edu.java.bot.annotation.URIConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

public record LinkUpdateRequest(
    @Min(1)
    Long id,
    @URIConstraint
    URI url,
    @NotNull
    String description,
    @NotNull
    List<Long> tgChatIds
) {}
