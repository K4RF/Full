package com.book.manage.service.book;

import com.book.manage.entity.Book;
import com.book.manage.entity.Category;
import com.book.manage.entity.Member;
import com.book.manage.entity.dto.BookEditDto;
import com.book.manage.entity.dto.BookSearchDto;
import com.book.manage.repository.book.BookRepository;
import com.book.manage.service.book.category.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final CategoryService categoryService;

    @Override
    public Book save(Book book, Set<String> tagNames) {
        // 1. 추후 카테고리 적용을 위해 먼저 저장 시도
        Book savedBook = bookRepository.save(book);

        // 2. Category 생성 후 저장
        List<Category> categories = categoryService.createCategories(tagNames, savedBook);
        savedBook.setTags(categories);

        // 3. Category 포함하여 업데이트
        return bookRepository.save(savedBook);
    }

    @Override
    public Optional<Book> findById(Long bookId) {
        // 도서 조회
        return bookRepository.findById(bookId);
    }

    @Override
    public Book edit(Long bookId, BookEditDto editParam) {
        // 도서 조회
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));

        // 도서 정보 업데이트
        book.setTitle(editParam.getTitle());
        book.setAuthor(editParam.getAuthor());
        book.setPublisher(editParam.getPublisher());
        book.setDetails(editParam.getDetails());

        // 카테고리 정보 업데이트
        List<Category> updatedCategories = categoryService.updateCategories(editParam.getCategories(), book);

        // 기존 카테고리와 비교해 사라진 카테고리 삭제
        categoryService.changeDelete(book, updatedCategories);
        // 도서 저장
        return bookRepository.save(book);
    }

    @Override
    public void deleteById(Long bookId) {
        Optional<Book> book = bookRepository.findById(bookId);
        if (book.isPresent()) {
            // 삭제 로직
            bookRepository.deleteById(bookId);
            log.info("Successfully deleted member with ID: {} and updated their posts to 'deleteUser'", bookId);
        } else {
            log.warn("Failed to delete member. No member found with ID: {}", bookId);

        }
    }

    public List<Book> findBooks(BookSearchDto searchDto) {
        return bookRepository.findAll(searchDto);
    }

    public List<String> getAvailableCategories() {
        return bookRepository.findDistinctCategories();
    }
}
