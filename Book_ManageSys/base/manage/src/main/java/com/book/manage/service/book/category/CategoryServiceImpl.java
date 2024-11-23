package com.book.manage.service.book.category;

import com.book.manage.entity.Book;
import com.book.manage.entity.Category;
import com.book.manage.repository.book.category.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public List<Category> createCategories(Set<String> tagNames, Book book) {
        List<Category> categories = new ArrayList<>();
        for (String tag : tagNames) {
            Category category = categoryRepository.findByTagAndBookId(tag, book.getBookId())
                    .orElseGet(() -> {
                        Category newCategory = new Category(tag, book);
                        return categoryRepository.save(newCategory);
                    });
        }
        return categories;
    }

    @Override
    public List<Category> updateCategories(List<Category> tagsToUpdate, Book book) {
        List<Category> updatedCategories = new ArrayList<>();
        for (Category category : tagsToUpdate) {
            Category existingCat = categoryRepository.findByTagAndBookId(category.getTag(), book.getBookId())
                    .orElseGet(() -> {
                        Category newCategory = new Category(category.getTag(), book);
                        return categoryRepository.save(newCategory);
                    });
            updatedCategories.add(existingCat);
        }
        return updatedCategories;
    }

    @Transactional
    public void changeDelete(Book book, List<Category> updatedTags) {
        // 업데이트된 태그 이름만 추출
        List<String> updatedTagNames = updatedTags.stream()
                .map(Category::getTag)
                .collect(Collectors.toList());

        // 삭제할 태그 처리
        categoryRepository.updateDelete(book.getBookId(), updatedTagNames);
    }
}
