package com.book.manage.entity.dto;

import com.book.manage.entity.Category;
import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class BookEditDto {
    private Long bookId;
    private String title;
    private String author;
    private String publisher;
    private String details;
    private Set<Category> categories = new HashSet<>();  // Set<Category>로 변경


    private String imagePath; // 이미지 경로를 저장하는 필드

    @DateTimeFormat(pattern = "yyyy-MM-dd") // HTML5 기본 날짜 형식
    private LocalDate publishDate;

    public BookEditDto() {}

    // 생성자
    public BookEditDto(Long bookId, String title, String author, String publisher, String details, Set<Category> categories, String imagePath, LocalDate publishDate) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.details = details;
        this.categories = categories;
        this.imagePath = imagePath;
        this.publishDate = getPublishDate();
    }

    public String getCategoryFormatted() {
        return categories.stream()
                .map(Category::getCate)
                .collect(Collectors.joining(", "));
    }

    public void setCategoryFormatted(String tagsFormatted) {
        this.categories = Arrays.stream(tagsFormatted.split(","))
                .map(String::trim)
                .map(cate -> new Category(cate))  // 직접 Category 객체를 생성
                .collect(Collectors.toSet());  // Set<Category>로 저장
    }
}
