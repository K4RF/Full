package com.book.manage.service.category;

import com.book.manage.entity.Book;
import com.book.manage.entity.Category;
import com.book.manage.repository.category.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> createCategories(Set<String> cateNames, Book book) {
        List<Category> categories = new ArrayList<>();
        for (String cateName : cateNames) {
            Category category = categoryRepository.findByTagAndBookId(cateName, book.getBookId())
                    .orElseGet(() -> {
                        Category newCategory = new Category(cateName, book);
                        return categoryRepository.save(newCategory);
                    });
            categories.add(category);
        }
        return categories;
    }

    @Override
    public List<Category> updateCategories(List<Category> cateToUpdate, Book book) {
        List<Category> updatedCategories = new ArrayList<>();
        for (Category category : cateToUpdate) {
            Category existingCate = categoryRepository.findByTagAndBookId(category.getCate(), book.getBookId())
                    .orElseGet(() -> {
                        Category newCategory = new Category(category.getCate(), book);
                        return categoryRepository.save(newCategory);
                    });
            updatedCategories.add(existingCate);
        }
        return updatedCategories;
    }

    @Override
    public boolean hasDuplicateCates(List<String> cateNames) {
        Set<String> cateSet = new HashSet<>(cateNames);
        return cateSet.size() != cateNames.size();
    }
    @Override
    @Transactional
    public void changeDelete(Book book, List<Category> updatedCategories) {
        // 업데이트된 태그 이름만 추출
        List<String> updatedCateNames = updatedCategories.stream()
                .map(Category::getCate)
                .collect(Collectors.toList());

        // 삭제할 태그 처리
        categoryRepository.updateDelete(book.getBookId(), updatedCateNames);
    }

    public void delete(Long bookId) {
        categoryRepository.deleteByBookId(bookId);
    }
}
