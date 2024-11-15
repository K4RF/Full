package com.book.manage.service.book;

import com.book.manage.entity.Book;
import com.book.manage.repository.book.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class BookServiceImplTest {

    @Autowired
    private BookServiceImpl bookService;

    @Autowired
    private BookRepository bookRepository;

    private Book defaultBook;

    @BeforeEach
    void setUp() {
        // 기본 데이터 저장
        defaultBook = new Book();
        defaultBook.setTitle("Default Book Title");
        defaultBook.setAuthor("Default Author");
        defaultBook.setPublisher("Default Publisher");
        defaultBook.setDetails("This is a default book for testing.");
        defaultBook = bookRepository.save(defaultBook); // 데이터 저장
    }

    @Test
    void save_shouldSaveAndReturnBook() {
        // Given
        Book newBook = new Book();
        newBook.setTitle("Spring Boot Guide");
        newBook.setAuthor("AuthorName");
        newBook.setPublisher("PublisherName");
        newBook.setDetails("Comprehensive guide on Spring Boot");

        // When
        Book savedBook = bookService.save(newBook);

        // Then
        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Spring Boot Guide");
        assertThat(savedBook.getAuthor()).isEqualTo("AuthorName");
        assertThat(savedBook.getPublisher()).isEqualTo("PublisherName");
        assertThat(savedBook.getDetails()).isEqualTo("Comprehensive guide on Spring Boot");
    }

    @Test
    void findById_shouldReturnBookWhenBookExists() {
        // Given
        Long bookId = defaultBook.getBookId();

        // When
        Optional<Book> foundBook = bookService.findById(bookId);

        // Then
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo(defaultBook.getTitle());
        assertThat(foundBook.get().getAuthor()).isEqualTo(defaultBook.getAuthor());
        assertThat(foundBook.get().getPublisher()).isEqualTo(defaultBook.getPublisher());
        assertThat(foundBook.get().getDetails()).isEqualTo(defaultBook.getDetails());
    }

    @Test
    void findById_shouldReturnEmptyWhenBookDoesNotExist() {
        // Given
        Long nonExistentBookId = 999L;

        // When
        Optional<Book> foundBook = bookService.findById(nonExistentBookId);

        // Then
        assertThat(foundBook).isEmpty();
    }
}
