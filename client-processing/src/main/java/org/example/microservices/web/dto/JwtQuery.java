package org.example.microservices.web.dto;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class JwtQuery {
    private String accessToken;
}