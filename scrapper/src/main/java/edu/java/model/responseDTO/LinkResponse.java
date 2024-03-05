package edu.java.model.responseDTO;

import java.net.URI;

public record LinkResponse(
    Long id,
    URI url
) {}
