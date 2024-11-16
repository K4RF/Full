package com.book.manage.repository.book;

import com.book.manage.entity.Book;
import com.book.manage.entity.Member;
import com.book.manage.entity.dto.BookEditDto;
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

    @Override
    public void edit(Long bookId, BookEditDto editParam) {
        Book findBook = findById(bookId).orElseThrow();
        findBook.setTitle(editParam.getTitle());
        findBook.setAuthor(editParam.getAuthor());
        findBook.setPublisher(editParam.getPublisher());
        findBook.setDetails(editParam.getDetails());
    }

    @Override
    public void deleteById(Long bookId) {
        log.info("Attempting to delete book with ID: {}", bookId);  // 삭제 시도 로그
        Book book = em.find(Book.class, bookId);
        if (book != null) {
            log.info("Found member with ID: {}. Proceeding with deletion.", bookId);
            em.remove(book);
            log.info("Deleted member with ID: {}", bookId);  // 삭제 완료 로그
        } else {
            log.warn("No member found with ID: {}", bookId);  // 삭제 실패 로그
        }
    }
}
