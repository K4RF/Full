package com.book.manage.service.book;

import com.book.manage.entity.Book;

import java.util.Optional;

public interface BookService {
    Book save(Book book);

    Optional<Book> findById(Long bookId);
}
