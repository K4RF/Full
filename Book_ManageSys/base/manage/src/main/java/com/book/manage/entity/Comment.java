package com.book.manage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @NotEmpty
    private String writer;

    @NotEmpty
    private String review;

    public Comment(){

    }

    public Comment(Book book, String writer, String review) {
        this.book = book;
        this.writer = writer;
        this.review = review;
    }
}
