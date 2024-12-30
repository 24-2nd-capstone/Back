package com.bustling.book.service;

import com.bustling.auth.entity.User;
import com.bustling.book.dto.BookDto;
import com.bustling.book.entity.Book;
import com.bustling.book.repository.BookRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    public List<Book> getBook() {
        return bookRepository.findAll();
    }

    @Transactional
    public ResponseEntity<Book> getBook(Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);

        return optionalBook.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public void createBook(User user, BookDto dto, MultipartFile image) throws IOException {
        Book book = new Book(
                null,
                user,
                dto.getTransType(),
                null,
                dto.getBookTitle(),
                dto.getWriters(),
                dto.getPublisher(),
                dto.getGenre(),
                dto.getStatus(),
                dto.getMemo(),
                LocalDateTime.now()
        );
        bookRepository.save(book);
    }

    private ResponseEntity<?> checkBookOwnership(Long id, User user) {
        Optional<Book> optionalBook = bookRepository.findById(id);

        if (optionalBook.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Book book = optionalBook.get();
        if (!book.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("내 글만 수정, 삭제 할 수 있습니다");
        }

        return ResponseEntity.ok(book);
    }

    public ResponseEntity<?> deleteBook(Long id, User user) {
        ResponseEntity<?> response = checkBookOwnership(id, user);
        if (response.getStatusCode() != HttpStatus.OK) {
            return response;
        }

        Book article = (Book) response.getBody();
        bookRepository.delete(article);

        return ResponseEntity.ok().build();
    }

}
