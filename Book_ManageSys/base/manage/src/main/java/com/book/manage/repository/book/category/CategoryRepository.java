package com.book.manage.repository.book.category;

import com.book.manage.entity.Book;
import com.book.manage.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findByTagAndBookId(String cate, Long bookId);
    void updateDelete(Long bookId, List<String> cateNames);
    void deleteByBookId(Long bookId);
    List<Category> findAll();
    Optional<Category> findByCate(String cate);
    List<Category> findByBook(Book book);
}
