package com.book.manage.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

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
    public double calculateAverageRating() {
        return comments.stream()
                .mapToInt(Comment::getRating)
                .average()
                .orElse(0.0);
    }

}
