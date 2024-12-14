package com.book.manage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @NotEmpty
    private String writer;

    @NotEmpty
    private String review;

    @Min(1)
    @Max(5)
    @Column(nullable = false) // 별점은 1~5 사이 값
    private int rating;


    public Comment(){

    }

    public Comment(Book book, String writer, String review, int rating) {
        this.book = book;
        this.writer = writer;
        this.review = review;
        this.rating = rating;
    }
}
