package com.book.manage.service.book;

import com.book.manage.entity.Book;
import com.book.manage.entity.dto.BookEditDto;

import java.util.Optional;

public interface BookService {
    Book save(Book book);

    Optional<Book> findById(Long bookId);
    Book edit(Long bookId, BookEditDto editParam);
    void deleteById(Long bookId);
}
