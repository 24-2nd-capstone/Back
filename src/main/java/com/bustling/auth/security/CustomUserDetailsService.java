package com.bustling.auth.security;

import com.bustling.auth.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        if (!userRepository.existsById(id)) {
            throw new UsernameNotFoundException(id + "에 해당하는 사용자가 존재하지 않습니다.");
        }
        return (UserDetails) userRepository.findById(id).get();
    }
}
