package com.book.manage.repository.book;

import com.book.manage.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Book save(Book book);
    Optional<Book> findById(Long bookId);
}
