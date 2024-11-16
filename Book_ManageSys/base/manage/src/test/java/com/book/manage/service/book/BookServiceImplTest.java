package com.book.manage.service.book;

import com.book.manage.entity.Book;
import com.book.manage.entity.dto.BookEditDto;
import com.book.manage.repository.book.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
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
        // Log savedBook 정보 출력
        log.info("Saved Book Info: {}", savedBook);
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

    @Test
    void edit_shouldUpdateBookDetails() {
        // Given
        Book existingBook = bookRepository.findById(defaultBook.getBookId()) // 기본 데이터를 저장한 후 ID를 통해 조회
                .orElseThrow(() -> new RuntimeException("Test setup failed: Book not found"));
        Long bookId = existingBook.getBookId();

        // 수정할 데이터 준비
        BookEditDto editParam = new BookEditDto();
        editParam.setTitle("Updated Title");
        editParam.setAuthor("Updated Author");
        editParam.setPublisher("Updated Publisher");
        editParam.setDetails("Updated Details");

        // When
        Book updatedBook = bookService.edit(bookId, editParam);

        // Then
        assertThat(updatedBook).isNotNull();
        assertThat(updatedBook.getBookId()).isEqualTo(bookId);
        assertThat(updatedBook.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedBook.getAuthor()).isEqualTo("Updated Author");
        assertThat(updatedBook.getPublisher()).isEqualTo("Updated Publisher");
        assertThat(updatedBook.getDetails()).isEqualTo("Updated Details");

        // 로그 출력
        log.info("Updated book: {}", updatedBook);
    }


    @Test
    void edit_shouldThrowExceptionWhenBookNotFound() {
        // Given
        Long nonExistentBookId = 999L; // 존재하지 않는 ID
        BookEditDto editParam = new BookEditDto();
        editParam.setTitle("New Title");
        editParam.setAuthor("New Author");
        editParam.setPublisher("New Publisher");
        editParam.setDetails("New Details");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookService.edit(nonExistentBookId, editParam));

        assertThat(exception.getMessage()).isEqualTo("Book not found");
    }
}
