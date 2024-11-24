package com.book.manage.service.book;

import com.book.manage.entity.Book;
import com.book.manage.entity.Category;
import com.book.manage.entity.dto.BookEditDto;
import com.book.manage.repository.book.BookRepository;
import com.book.manage.service.book.category.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    @Autowired
    private CategoryService categoryService;

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
    void save_shouldSaveBookWithCategories() {
        // Given
        Book newBook = new Book();
        newBook.setTitle("Spring Boot Guide");
        newBook.setAuthor("AuthorName");
        newBook.setPublisher("PublisherName");
        newBook.setDetails("Comprehensive guide on Spring Boot");
        Set<String> categories = Set.of("Programming", "Technology");

        // When
        Book savedBook = bookService.save(newBook, categories);

        // Then
        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Spring Boot Guide");
        assertThat(savedBook.getTags()).hasSize(categories.size());
        assertThat(savedBook.getTags())
                .extracting(Category::getTag)
                .containsExactlyInAnyOrderElementsOf(categories);

        log.info("Saved Book with Categories: {}", savedBook.getTags());
    }

    @Test
    void edit_shouldUpdateBookAndCategories() {
        // Given
        Long bookId = defaultBook.getBookId();
        BookEditDto editParam = new BookEditDto();
        editParam.setTitle("Updated Title");
        editParam.setAuthor("Updated Author");
        editParam.setPublisher("Updated Publisher");
        editParam.setDetails("Updated Details");
       // editParam.setCategories(Set.of("Fiction", "Drama"));

        // When
        Book updatedBook = bookService.edit(bookId, editParam);

        // Then
        assertThat(updatedBook).isNotNull();
        assertThat(updatedBook.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedBook.getTags())
                .extracting(Category::getTag)
                .containsExactlyInAnyOrder("Fiction", "Drama");

        log.info("Updated Book with Categories: {}", updatedBook.getTags());
    }

    @Test
    void deleteById_shouldDeleteBookAndCategories() {
        // Given
        Long bookId = defaultBook.getBookId();
        Set<String> categories = Set.of("Programming", "Science");
        bookService.save(defaultBook, categories);

        // When
        bookService.deleteById(bookId);

        // Then
        Optional<Book> deletedBook = bookRepository.findById(bookId);
        assertThat(deletedBook).isEmpty(); // 도서 삭제 확인

//        List<Category> remainingCategories = categoryService.fin(bookId);
//        assertThat(remainingCategories).isEmpty(); // 카테고리 삭제 확인

        log.info("Deleted Book and Categories for Book ID: {}", bookId);
    }

    @Test
    void findById_shouldReturnBookWithCategories() {
        // Given
        Long bookId = defaultBook.getBookId();
        Set<String> categories = Set.of("Fiction", "Adventure");
        bookService.save(defaultBook, categories);

        // When
        Optional<Book> foundBook = bookService.findById(bookId);

        // Then
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTags())
                .extracting(Category::getTag)
                .containsExactlyInAnyOrder("Fiction", "Adventure");

        log.info("Found Book with Categories: {}", foundBook.get().getTags());
    }
}
