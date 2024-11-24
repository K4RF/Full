package com.book.manage.service.book.category;

import com.book.manage.entity.Book;
import com.book.manage.entity.Category;

import java.util.List;
import java.util.Set;

public interface CategoryService {
    List<Category> createCategories(Set<String> cateNames, Book book);
    List<Category> updateCategories(List<Category> tagsToUpdate, Book book);
    void changeDelete(Book book, List<Category> updatedCategories);
    void delete(Long bookId);
    boolean hasDuplicateCates(List<String> cateNames);
}
