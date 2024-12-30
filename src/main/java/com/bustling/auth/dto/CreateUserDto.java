package com.bustling.auth.dto;

import lombok.Getter;

@Getter
public class CreateUserDto {
    private String id;
    private String name;
    private String password;
    private String phone;
}
