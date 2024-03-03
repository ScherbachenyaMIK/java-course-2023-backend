package edu.java.model.responseDTO;

import java.util.List;

public record ListLinksResponse(
    List<LinkResponse> links,
    int size
) {}
