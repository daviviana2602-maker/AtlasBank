package org.atlas.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor


public class JwtDataFormat {

    private String userId;
    private String role;
    private String type;

}