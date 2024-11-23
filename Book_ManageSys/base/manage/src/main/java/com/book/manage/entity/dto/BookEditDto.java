package com.book.manage.entity.dto;

import com.book.manage.entity.Category;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class BookEditDto {
    private Long bookId;
    private String title;
    private String author;
    private String publisher;
    private String details;
    private List<Category> categories = new ArrayList<>();


    public BookEditDto() {}
    // 생성자
    public BookEditDto(Long bookId, String title, String author, String publisher, String details, List<Category> categories) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.details = details;
        this.categories = categories;
    }

    public String getCategoryFormatted() {
        return categories.stream()
                .map(Category::getTag)
                .collect(Collectors.joining(", "));
    }

    public void setCategoryFormatted(String tagsFormatted) {
        this.categories = Arrays.stream(tagsFormatted.split(","))
                .map(String::trim)
                .map(Category::new)
                .collect(Collectors.toList());
    }
}
