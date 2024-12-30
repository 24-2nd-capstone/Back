package com.bustling.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenDto {
    private String AccessToken;
    private String RefreshToken;
}
