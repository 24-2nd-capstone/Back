package com.bustling.chat.service;

import com.bustling.auth.entity.User;
import com.bustling.chat.dto.MessageDto;
import com.bustling.chat.entity.ChatMessage;
import com.bustling.chat.entity.ChatRoom;
import com.bustling.chat.repository.ChatMessageRepository;
import com.bustling.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository ChatMessageRepository;

    @Transactional
    public void saveMessage(User user, MessageDto dto) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findByRoomId(dto.getChatRoomId());
        ChatRoom chatRoom = chatRoomOptional.get();

        ChatMessage chat = new ChatMessage(
                0L,
                chatRoom,
                user.getName(),
                dto.getMessage(),
                LocalDateTime.now()
        );
        ChatMessageRepository.save(chat);
    }
}
