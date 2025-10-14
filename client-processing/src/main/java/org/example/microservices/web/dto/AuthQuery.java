package org.example.microservices.web.dto;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthQuery {

    private String username;
    private String password;
}
