package edu.java.model.requestDTO;

import edu.java.annotation.URIConstraint;
import java.net.URI;

public record RemoveLinkRequest(
    @URIConstraint
    URI link
) {}
