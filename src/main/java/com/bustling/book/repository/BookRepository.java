package com.bustling.book.repository;

import com.bustling.auth.entity.User;
import com.bustling.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByRentedBy(User user);
}
