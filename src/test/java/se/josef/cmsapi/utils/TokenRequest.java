package se.josef.cmsapi.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenRequest {
    private String token;
    private String returnSecureToken;
}
