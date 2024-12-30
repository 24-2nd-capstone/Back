package com.bustling.chat.service;

import com.bustling.auth.entity.User;
import com.bustling.chat.dto.ChatRoomIdDto;
import com.bustling.chat.entity.ChatMessage;
import com.bustling.chat.entity.ChatRoom;
import com.bustling.chat.repository.ChatMessageRepository;
import com.bustling.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public ResponseEntity<?> createChatRoom(Long referenceId,  User user) {
        if (user == null || referenceId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 요청입니다");
        }

        Optional<ChatRoom> existingChatRoom = chatRoomRepository.findByReferenceIdAndPostOwnerAndUser(referenceId, postOwner, user);
        if (existingChatRoom.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ChatRoomIdDto(existingChatRoom.get().getRoomId(), getChatRoomTitle(existingChatRoom.get()), getChatPartnerName(existingChatRoom.get(), user)));
        }

        ChatRoom chatRoom = new ChatRoom(
                null,
                referenceId,
                postOwner,
                user,
                UUID.randomUUID().toString(),
                LocalDateTime.now()
        );
        chatRoomRepository.save(chatRoom);
        return ResponseEntity.status(HttpStatus.OK).body(new ChatRoomIdDto(chatRoom.getRoomId(), getChatRoomTitle(chatRoom), getChatPartnerName(chatRoom, user)));
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getChatMessagesByRoomId(String roomId) {
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findByRoomId(roomId);
        if (optionalChatRoom.isPresent()) {
            return chatMessageRepository.findByChatRoomId(optionalChatRoom.get().getId());
        }
        throw new IllegalArgumentException("채팅방을 찾을 수 없습니다");
    }

    @Transactional(readOnly = true)
    public List<ChatRoomIdDto> getChatRoomsForUser(User user) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByPostOwnerOrUser(user, user);
        return chatRooms.stream()
                .map(chatRoom -> new ChatRoomIdDto(chatRoom.getRoomId(), getChatRoomTitle(chatRoom), getChatPartnerName(chatRoom, user)))
                .collect(Collectors.toList());
    }

    private String getChatRoomTitle(ChatRoom chatRoom) {
        // Assuming the referenceId can be used to fetch titles for specific contexts if needed
        return "Chat Room " + chatRoom.getReferenceId();
    }

    private String getChatPartnerName(ChatRoom chatRoom, User user) {
        return chatRoom.getPostOwner().getId().equals(user.getId()) ? chatRoom.getUser().getName() : chatRoom.getPostOwner().getName();
    }
}
