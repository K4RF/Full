package com.book.manage.repository.book;

import com.book.manage.entity.Book;
import com.book.manage.entity.Order;
import com.book.manage.entity.QBook;
import com.book.manage.entity.dto.BookEditDto;
import com.book.manage.entity.dto.BookSearchDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;


import java.util.List;
import java.util.Optional;

import static com.book.manage.entity.QBook.book;
import static com.book.manage.entity.QOrder.order;

@Repository
@Slf4j
public class BookJpaRepository implements BookRepository {
    private final EntityManager em;
    private final JPAQueryFactory query;

    public BookJpaRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
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
            log.info("Found book with ID: {}. Proceeding with deletion.", bookId);
            em.remove(book);
            log.info("Deleted book with ID: {}", bookId);  // 삭제 완료 로그
        } else {
            log.warn("No book found with ID: {}", bookId);  // 삭제 실패 로그
        }
    }

    @Override
    public List<Book> findAll(BookSearchDto searchParam) {
        String author = searchParam.getAuthor();
        String title = searchParam.getTitle();
        String category = searchParam.getCategory();

        List<Book> result = query
                .select(book)
                .from(book)
                .where(
                        likeTitle(title),
                        likeAuthor(author),
                        likeCategory(category) // 카테고리 조건 추가
                )
                .fetch();
        return result;
    }

    private BooleanExpression likeTitle(String title) {
        return StringUtils.hasText(title) ? book.title.like("%" + title + "%") : null;
    }

    private BooleanExpression likeAuthor(String author) {
        return StringUtils.hasText(author) ? book.author.like("%" + author + "%") : null;
    }

    private BooleanExpression likeCategory(String category) {
        return StringUtils.hasText(category) ? book.categories.any().cate.like("%" + category + "%") : null;
    }

    
    @Override
    public List<Book> findAllBook() {
        return query.selectFrom(book)
                .fetch();
    }
}
