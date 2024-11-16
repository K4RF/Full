package com.book.manage.entity.dto;

import lombok.Data;

@Data
public class BookEditDto {
    private Long bookId;
    private String title;
    private String author;
    private String publisher;
    private String details;

    // 생성자
    public BookEditDto(Long bookId, String title, String author, String publisher, String details) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.details = details;
    }
}
