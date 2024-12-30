package com.bustling.book.entity;

import com.bustling.auth.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "articles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private int transType;
    private String imageUrl;
    private String bookTitle;
    private String writers;
    private String publisher;
    private String genre;
    private int status;
    private String memo;
    private LocalDateTime createdAt;
}