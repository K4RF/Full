package com.book.manage.repository.book;

import com.book.manage.entity.Book;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
public class BookJpaRepository implements BookRepository{
    private final EntityManager em;
    private final JPAQueryFactory factory;

    public BookJpaRepository(EntityManager em) {
        this.em = em;
        this.factory = new JPAQueryFactory(em);
    }

    @Override
    public Book save(Book book) {
        em.persist(book);
        return book;
    }

    @Override
    public Optional<Book> findById(Long bookId) {
        Book book = em.find(Book.class, bookId);
        return Optional.ofNullable(book);
    }
}
