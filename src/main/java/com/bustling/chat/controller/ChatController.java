package com.bustling.chat.controller;


import com.bustling.auth.entity.User;
import com.bustling.chat.dto.ChatRoomIdDto;
import com.bustling.chat.dto.MessageDto;
import com.bustling.chat.entity.ChatMessage;
import com.bustling.chat.service.ChatMessageService;
import com.bustling.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @PostMapping("/create/{referenceId}")
    public ResponseEntity<?> createChatRoom(
            @PathVariable Long referenceId,
            @AuthenticationPrincipal User user
    ) {
        return chatRoomService.createChatRoom(referenceId, user);
    }

    @PostMapping("/sendMessage")
    public void sendMessage(
            @RequestBody MessageDto dto,
            @AuthenticationPrincipal User user
    ) {
        chatMessageService.saveMessage(user, dto);
    }

    @GetMapping("/messages/{roomId}")
    public ResponseEntity<List<ChatMessage>> getChatMessages(
            @PathVariable String roomId
    ) {
        List<ChatMessage> messages = chatRoomService.getChatMessagesByRoomId(roomId);
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }

    @GetMapping("/myRooms")
    public ResponseEntity<List<ChatRoomIdDto>> getMyChatRooms(
            @AuthenticationPrincipal User currentUser
    ) {
        List<ChatRoomIdDto> chatRooms = chatRoomService.getChatRoomsForUser(currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(chatRooms);
    }
}
