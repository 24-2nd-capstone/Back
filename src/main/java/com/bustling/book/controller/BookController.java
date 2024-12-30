package com.bustling.book.controller;

import com.bustling.auth.entity.User;
import com.bustling.book.dto.BookDto;
import com.bustling.book.entity.Book;
import com.bustling.book.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/books")
@AllArgsConstructor
public class BookController {
    private BookService bookService;

    @GetMapping
    public List<Book> getArticles() {
        return bookService.getBook();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getArticle(
            @PathVariable Long id
    ) {
        return bookService.getBook(id);
    }

    @PostMapping
    public void createArticle(
            @AuthenticationPrincipal User user,
            @RequestPart BookDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        bookService.createBook(user, dto, image);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticle(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        return bookService.deleteBook(id, user);
    }
}
