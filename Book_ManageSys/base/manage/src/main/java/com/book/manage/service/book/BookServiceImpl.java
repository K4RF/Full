package com.book.manage.service.book;

import com.book.manage.entity.Book;
import com.book.manage.entity.Member;
import com.book.manage.entity.dto.BookEditDto;
import com.book.manage.entity.dto.BookSearchDto;
import com.book.manage.repository.book.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Override
    public Book save(Book book) {
        // 1. 추후 카테고리 적용을 위해 먼저 저장 시도
        Book savedBook = bookRepository.save(book);

        // x. 카테고리 설정 후 저장 추후에 추가 예정
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

    @Override
    public List<Book> findBooks(BookSearchDto searchParam) {
        return bookRepository.findAll(searchParam);
    }

    @Override
    public List<Book> getBooksByCategory(String category) {
        return bookRepository.findByCategory(category);
    }
}
