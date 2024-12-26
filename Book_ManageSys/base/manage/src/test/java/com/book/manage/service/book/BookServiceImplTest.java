package com.book.manage.service.book;

import com.book.manage.entity.Book;
import com.book.manage.entity.Category;
import com.book.manage.entity.dto.BookEditDto;
import com.book.manage.repository.book.BookRepository;
import com.book.manage.repository.category.CategoryRepository;
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

@SpringBootTest
@Transactional
@Slf4j
class BookServiceImplTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Book defaultBook;

    @BeforeEach
    void setUp() {
        // 테스트용 기본 데이터 생성
        defaultBook = new Book();
        defaultBook.setTitle("Default Book Title");
        defaultBook.setAuthor("Default Author");
        defaultBook.setPublisher("Default Publisher");
        defaultBook.setDetails("This is a default book for testing.");
        defaultBook = bookRepository.save(defaultBook); // DB 저장
        log.info("Created default book: {}", defaultBook);
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

        log.info("Saving new book: {}", newBook);

        // When
        Book savedBook = bookService.save(newBook, categories);
        log.info("Book saved successfully: {}", savedBook);

        // Then
        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Spring Boot Guide");

        // 카테고리 확인
        Set<Category> savedCategories = savedBook.getCategories();  // Set으로 변경
        log.info("Saved categories: {}", savedCategories);
        assertThat(savedCategories).hasSize(2);
        assertThat(savedCategories)
                .extracting(Category::getCate)
                .containsExactlyInAnyOrderElementsOf(categories);
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
        editParam.setCategories(Set.of(
                new Category("Fiction", defaultBook),
                new Category("Drama", defaultBook)
        ));

        log.info("Editing book with ID {}: {}", bookId, editParam);

        // When
        Book updatedBook = bookService.edit(bookId, editParam);
        log.info("Updated book: {}", updatedBook);

        // Then
        assertThat(updatedBook).isNotNull();
        assertThat(updatedBook.getTitle()).isEqualTo("Updated Title");

        // 업데이트된 카테고리 확인
        Set<Category> updatedCategories = updatedBook.getCategories();  // Set으로 변경
        log.info("Updated categories: {}", updatedCategories);
        assertThat(updatedCategories).hasSize(2);
        assertThat(updatedCategories)
                .extracting(Category::getCate)
                .containsExactlyInAnyOrder("Fiction", "Drama");
    }

    @Test
    void deleteById_shouldDeleteBookAndCategories() {
        // Given
        Long bookId = defaultBook.getBookId();
        Set<String> categories = Set.of("Programming", "Science");
        bookService.save(defaultBook, categories);
        log.info("Deleting book with ID {}", bookId);

        // When
        bookService.deleteById(bookId);

        // Then
        Optional<Book> deletedBook = bookRepository.findById(bookId);
        log.info("Deleted book: {}", deletedBook);
        assertThat(deletedBook).isEmpty(); // 도서 삭제 확인

        // 카테고리 삭제 확인
        List<Category> remainingCategories = categoryRepository.findAll();
        log.info("Remaining categories after deletion: {}", remainingCategories);
        assertThat(remainingCategories).isEmpty();
    }

    @Test
    void findById_shouldReturnBookWithCategories() {
        // Given
        Long bookId = defaultBook.getBookId();
        Set<String> categories = Set.of("Fiction", "Adventure");
        bookService.save(defaultBook, categories);
        log.info("Finding book with ID {}", bookId);

        // When
        Optional<Book> foundBook = bookService.findById(bookId);
        log.info("Found book: {}", foundBook);

        // Then
        assertThat(foundBook).isPresent();
        Set<Category> foundCategories = foundBook.get().getCategories();  // Set으로 변경
        log.info("Found categories for book: {}", foundCategories);
        assertThat(foundCategories)
                .extracting(Category::getCate)
                .containsExactlyInAnyOrder("Fiction", "Adventure");
    }

    @Test
    void findAll_shouldReturnAllCategories() {
        // Given
        Category category1 = new Category("Fiction", defaultBook);
        Category category2 = new Category("Adventure", defaultBook);
        categoryRepository.save(category1);
        categoryRepository.save(category2);

        log.info("Fetching all categories...");

        // When
        List<Category> allCategories = categoryRepository.findAll();
        log.info("All categories: {}", allCategories);

        // Then
        assertThat(allCategories).hasSize(2);
        assertThat(allCategories)
                .extracting(Category::getCate)
                .containsExactlyInAnyOrder("Fiction", "Adventure");
    }
}
