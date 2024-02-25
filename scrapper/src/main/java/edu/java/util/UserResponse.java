package edu.java.util;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private OffsetDateTime time;
    private String identifier;
}
