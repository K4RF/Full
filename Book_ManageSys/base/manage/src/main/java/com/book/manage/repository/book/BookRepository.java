package com.book.manage.repository.book;

import com.book.manage.entity.Book;
import com.book.manage.entity.dto.BookEditDto;
import com.book.manage.entity.dto.BookSearchDto;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Book save(Book book);
    Optional<Book> findById(Long bookId);
    void edit(Long bookId, BookEditDto editParam);
    void deleteById(Long bookId);
    List<Book> findAll(BookSearchDto searchParam);
}
