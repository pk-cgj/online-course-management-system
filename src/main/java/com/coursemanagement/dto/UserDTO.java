package com.coursemanagement.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String keycloakId;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
}
