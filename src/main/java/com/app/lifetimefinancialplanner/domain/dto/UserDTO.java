package com.app.lifetimefinancialplanner.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String name;

    // Mark the password field as write-only.
    // The password is accepted in incoming JSON requests
    // but will not be included in JSON responses when serializing the object.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
