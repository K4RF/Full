package com.book.manage.service.book;

import com.book.manage.entity.Book;
import com.book.manage.entity.Category;
import com.book.manage.entity.Member;
import com.book.manage.entity.dto.BookEditDto;
import com.book.manage.entity.dto.BookSearchDto;
import com.book.manage.repository.book.BookRepository;
import com.book.manage.repository.book.category.CategoryRepository;
import com.book.manage.service.book.category.CategoryService;
import com.book.manage.service.book.category.CategoryServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    @Override
    public Book save(Book book, Set<String> categories) {
        // 책 저장
        book = bookRepository.save(book);

        // 카테고리 순서 처리: 순서를 추가할 List 사용
        List<Category> categoryList = new ArrayList<>();
        int order = 0; // 카테고리 순서 초기화

        for (final String categoryName : categories) {
            Book finalBook = book;

            // 이미 존재하는 카테고리 조회, 없으면 새로 생성
            Category category = categoryRepository.findByCate(categoryName)
                    .orElseGet(() -> new Category(categoryName, finalBook));

            // 책과 카테고리 연결
            category.setBook(book);

            // 카테고리 순서 설정
            category.setCateOrder(order++);  // 순서 설정 후 증가
            categoryRepository.save(category);

            // List에 카테고리 추가
            categoryList.add(category);
        }

        // 책과 카테고리 연관 업데이트 후 리턴
        book.setCategories(new HashSet<>(categoryList));  // Set<Category> 타입으로 설정
        return book;
    }



    @Override
    public Optional<Book> findById(Long bookId) {
        // 도서 조회
        return bookRepository.findById(bookId);
    }

    public Book edit(Long bookId, BookEditDto bookEditDto) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        // 기본 필드 업데이트
        book.setTitle(bookEditDto.getTitle());
        book.setAuthor(bookEditDto.getAuthor());
        book.setPublisher(bookEditDto.getPublisher());
        book.setDetails(bookEditDto.getDetails());
        book.setImagePath(bookEditDto.getImagePath());

        // 카테고리 처리: 기존 카테고리 삭제 후 새로운 카테고리 추가
        Set<Category> updatedCategories = new HashSet<>();
        for (Category category : bookEditDto.getCategories()) {
            Optional<Category> existingCategory = categoryRepository.findByCate(category.getCate());

            // 카테고리가 이미 존재하면, 해당 카테고리 추가
            if (existingCategory.isPresent()) {
                updatedCategories.add(existingCategory.get());
            } else {
                // 존재하지 않으면 새로 생성하여 추가
                Category newCategory = new Category(category.getCate(), book);
                updatedCategories.add(newCategory);
            }
        }

        book.setCategories(updatedCategories);  // Set<Category> 형식으로 설정

        return bookRepository.save(book); // 책과 카테고리 저장
    }


    @Override
    public void deleteById(Long bookId) {
        Optional<Book> book = bookRepository.findById(bookId);
        if (book.isPresent()) {
            // 삭제 로직

            // 카테고리 삭제
            categoryService.delete(bookId);
            // 도서 삭제
            bookRepository.deleteById(bookId);
            log.info("Successfully deleted member with ID: {} and updated their posts to 'deleteUser'", bookId);
        } else {
            log.warn("Failed to delete member. No member found with ID: {}", bookId);

        }
    }

    public List<Book> findBooks(BookSearchDto searchDto) {
        return bookRepository.findAll(searchDto);
    }

}
