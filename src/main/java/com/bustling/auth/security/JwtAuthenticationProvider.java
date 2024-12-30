package com.bustling.auth.security;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();

        try {
            var claims = jwtUtil.getClaims(token);

            String userId = claims.getSubject();
            UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

            Object rolesObject = claims.get("roles");
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();

            if (rolesObject instanceof List<?>) {
                List<?> roles = (List<?>) rolesObject;
                authorities = roles.stream()
                        .filter(role -> role instanceof String)
                        .map(role -> new SimpleGrantedAuthority((String) role))
                        .collect(Collectors.toList());
            }

            return new JwtAuthentication(userDetails, token, authorities);
        } catch (Exception e) {
            throw new AuthenticationException("Invalid JWT token: " + e.getMessage()) {
            };
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthentication.class.isAssignableFrom(authentication);
    }
}
