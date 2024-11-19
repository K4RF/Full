package com.book.manage.entity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookSearchDto {
    private String title;
    private String author;


    public BookSearchDto() {}
    public BookSearchDto(String title, String author) {
        this.title = title;
        this.author = author;
    }
}
