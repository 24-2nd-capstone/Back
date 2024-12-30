package com.bustling.chat.dto;

import lombok.Getter;

@Getter
public class MessageDto {
    private String chatRoomId;
    private String messageType;
    private String message;
    private String sender;
}
