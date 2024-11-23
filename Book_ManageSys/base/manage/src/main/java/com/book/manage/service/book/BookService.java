package com.book.manage.service.book;

import com.book.manage.entity.Book;
import com.book.manage.entity.dto.BookEditDto;
import com.book.manage.entity.dto.BookSearchDto;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Book save(Book book);

    Optional<Book> findById(Long bookId);
    Book edit(Long bookId, BookEditDto editParam);
    void deleteById(Long bookId);
    List<Book> findBooks(BookSearchDto searchParam);
    List<Book> getBooksByCategory(String category);
}
