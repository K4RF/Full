package com.book.manage.entity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookSearchDto {
    private String title;
    private String author;
    private String category;


    public BookSearchDto() {}
    public BookSearchDto(String title, String author, String category) {
        this.title = title;
        this.author = author;
        this.category = category;
    }
}
