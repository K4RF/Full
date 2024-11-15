package com.book.manage.repository.book;

import com.book.manage.entity.Book;

import java.util.Optional;

public class BookJpaRepository implements BookRepository{
    private final Entity
    @Override
    public Book save(Book book) {
        return null;
    }

    @Override
    public Optional<Book> findById(Long id) {
        return Optional.empty();
    }
}
