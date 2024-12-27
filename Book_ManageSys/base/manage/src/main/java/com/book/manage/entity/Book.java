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
@EqualsAndHashCode(exclude = "categories")  // categories 필드를 제외하여 무한 참조 방지
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
    @DateTimeFormat(pattern = "yyyy-MM-dd") // HTML5 기본 날짜 형식
    private LocalDate publishDate;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_category",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @ToString.Exclude
    @OrderColumn(name = "cate_order")
    private Set<Category> categories = new HashSet<>();  // Set으로 변경


    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>();

    @Column(name = "total_comment")
    private int totalComment;  // 댓글 갯수를 저장하는 필드

    @Column(nullable = false)
    private double price; // 가격 필드 추가

    public String getCategoriesFormatted() {
        return categories.stream()
                .map(Category::getCate)
                .collect(Collectors.joining(", "));
    }

    public void setTagsFormatted(String tagsFormatted) {
        this.categories = Arrays.stream(tagsFormatted.split(","))
                .map(String::trim)
                .map(Category::new)
                .collect(Collectors.toSet());  // Set으로 변경
    }
    public void incrementViewCount() {
        this.viewCount++;
    }

    // 평균 별점 계산
    public BigDecimal calculateAverageRating() {
        if (comments.isEmpty()) {
            return BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP); // 댓글이 없을 경우 0.0 반환
        }

        return comments.stream()
                .map(Comment::getRating) // BigDecimal 스트림 생성
                .reduce(BigDecimal.ZERO, BigDecimal::add) // 합계를 계산
                .divide(
                        new BigDecimal(comments.size()), // 개수로 나누기
                        1, // 소수점 1자리까지
                        RoundingMode.HALF_UP // 반올림 방식 설정
                );
    }


    // 댓글 갯수 업데이트 메서드
    @PrePersist
    @PreUpdate
    public void updateTotalComment() {
        this.totalComment = comments.size();  // 댓글 갯수를 업데이트
    }
}
