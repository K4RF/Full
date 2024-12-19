package com.book.manage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

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
    @Size(max = 50) // 한 줄 정도의 길이로 제한 (예: 최대 150자)
    private String review;

    @DecimalMin("0.0")
    @DecimalMax("5.0")
    @Column(nullable = false, precision = 2, scale = 1) // 0.5 단위의 소수점 처리
    private BigDecimal rating;

    public Comment() {
    }

    public Comment(Book book, String writer, String review, BigDecimal rating) {
        this.book = book;
        this.writer = writer;
        this.review = review;
        this.rating = rating;
    }
}
