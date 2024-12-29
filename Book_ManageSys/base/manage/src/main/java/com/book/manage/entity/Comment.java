package com.book.manage.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    @Size(max = 50) // 한 줄 정도의 길이로 제한
    private String review;

    @DecimalMin("0.0")
    @DecimalMax("5.0")
    @Column(nullable = false, precision = 2, scale = 1) // 0.5 단위의 소수점 처리
    private BigDecimal rating;

    @Column(name = "review_date", nullable = false, updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd") // HTML5 기본 날짜 형식
    private LocalDate reviewDate = LocalDate.now(); // 기본값을 현재 날짜로 설정

    public Comment() {
    }

    public Comment(Book book, String writer, String review, BigDecimal rating) {
        this.book = book;
        this.writer = writer;
        this.review = review;
        this.rating = rating;
        this.reviewDate = LocalDate.now(); // 생성 시점의 날짜 저장
    }
}
