package com.book.manage.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;
@Data
@Entity
@EqualsAndHashCode(exclude = "categories")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private String details;

    @Column(name = "RENTAL_ABLE_BOOK", columnDefinition = "boolean default true")
    private Boolean rentalAbleBook;

    private String imagePath; // 이미지 경로를 저장하는 필드

    private int viewCount = 0; // 조회수 필드

    @Column(name = "publish_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishDate;

    @Column(nullable = false)
    private double price; // 가격 필드 추가

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_category",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @ToString.Exclude
    @OrderColumn(name = "cate_order")
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>();

    @Column(name = "total_comment")
    private int totalComment;

    // 평균 별점 계산
    public BigDecimal calculateAverageRating() {
        if (comments.isEmpty()) {
            return BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP);
        }

        return comments.stream()
                .map(Comment::getRating)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(
                        new BigDecimal(comments.size()),
                        1,
                        RoundingMode.HALF_UP
                );
    }

    // 댓글 갯수 업데이트 메서드
    @PrePersist
    @PreUpdate
    public void updateTotalComment() {
        this.totalComment = comments.size();
    }
}
