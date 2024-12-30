package com.bustling.auth.controller;


import com.bustling.auth.dto.CreateUserDto;
import com.bustling.auth.dto.LoginUserDto;
import com.bustling.auth.dto.ModifyUserDto;
import com.bustling.auth.entity.User;
import com.bustling.auth.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class UserController {
    private UserService userService;
    @PostMapping("/signup")
    public ResponseEntity<?> createUser(
            @RequestBody CreateUserDto dto
    ) {
        return userService.createUser(dto);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> loginUser(
            @RequestBody LoginUserDto dto
    ) {
        return userService.loginWithAuthenticationManager(dto);
    }

    @GetMapping("/me")
    public User getMyProfile(
            @AuthenticationPrincipal User user
    ) {
        return user;
    }

    @PutMapping
    public ResponseEntity<?> modifyUser(
            @RequestBody ModifyUserDto dto,
            @AuthenticationPrincipal User user
    ) {
        return userService.modifyUser(dto, user);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> deleteUser(
            @AuthenticationPrincipal User user
    ) {
        return userService.logoutUser(user);
    }
}

