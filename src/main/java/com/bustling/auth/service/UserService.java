package com.bustling.auth.service;

import com.bustling.auth.dto.CreateUserDto;
import com.bustling.auth.dto.LoginUserDto;
import com.bustling.auth.dto.ModifyUserDto;
import com.bustling.auth.dto.TokenDto;
import com.bustling.auth.entity.User;
import com.bustling.auth.repository.UserRepository;
import com.bustling.auth.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<?> createUser(CreateUserDto dto) {
        if (userRepository.findById(dto.getId()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 등록된 아이디 입니다");
        }
        User user = new User(
                dto.getId(),
                dto.getName(),
                dto.getPhone(),
                passwordEncoder.encode(dto.getPassword()),
                LocalDateTime.now()
        );
        userRepository.save(user);
        return ResponseEntity.ok().body("회원가입이 성공적으로 되었습니다");
    }

    public ResponseEntity<?> loginWithAuthenticationManager(LoginUserDto dto) {
        Optional<User> userOptional = userRepository.findById(dto.getId());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("존재하지 않는 아이디 입니다");
        }
        try {
            UsernamePasswordAuthenticationToken request = new UsernamePasswordAuthenticationToken(
                    dto.getId(), passwordEncoder.encode(dto.getPassword())
            );
            var result = authenticationManager.authenticate(request);
            User user = (User) result.getPrincipal();

            String accessToken = jwtUtil.generateAccessToken(user.getId());
            String refreshToken = jwtUtil.generateRefreshToken(user.getId());

            TokenDto token = new TokenDto(accessToken, refreshToken);
            return ResponseEntity.ok().body(token);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("올바르지 않은 비밀번호입니다");
        }
    }

    public ResponseEntity<?> refreshTokens(String refreshToken, String userId) {
        if (!jwtUtil.validateRefreshToken(refreshToken, userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 Refresh Token입니다");
        }

        String newAccessToken = jwtUtil.generateAccessToken(userId);
        String newRefreshToken = jwtUtil.generateRefreshToken(userId);

        TokenDto token = new TokenDto(newAccessToken, newRefreshToken);
        return ResponseEntity.ok().body(token);
    }

    public ResponseEntity<?> modifyUser(ModifyUserDto dto, User user) {
        user.setName(dto.getName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok().body("회원 정보가 성공적으로 수정되었습니다");
    }

    public ResponseEntity<?> logoutUser(User user) {
        String userId = user.getId();
        jwtUtil.invalidateRefreshToken(userId);
        return ResponseEntity.ok().body("로그아웃이 성공적으로 처리되었습니다");
    }
}
