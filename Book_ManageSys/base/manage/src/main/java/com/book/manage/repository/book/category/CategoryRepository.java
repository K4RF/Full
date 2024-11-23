package com.book.manage.repository.book.category;

import com.book.manage.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findByTagAndBookId(String tag, Long bookId);
    void updateDelete(Long bookId, List<String> tagNames);
}
