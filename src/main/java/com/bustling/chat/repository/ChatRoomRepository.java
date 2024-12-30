package com.bustling.chat.repository;

import com.bustling.chat.entity.ChatRoom;
import com.bustling.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByReferenceIdAndPostOwnerAndUser(Long referenceId, User postOwner, User user);

    Optional<ChatRoom> findByRoomId(String roomId);

    List<ChatRoom> findByPostOwnerOrUser(User postOwner, User user);
}
