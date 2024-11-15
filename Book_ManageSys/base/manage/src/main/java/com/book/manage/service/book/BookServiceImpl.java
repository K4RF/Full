package com.book.manage.service.book;

import com.book.manage.entity.Book;
import com.book.manage.repository.book.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
