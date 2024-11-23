package com.book.manage.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
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

    private String category;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_category",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @ToString.Exclude
    @OrderColumn(name = "book_cate")
    private List<Category> tags = new ArrayList<>();

    public String getCategoriesFormatted() {
        return tags.stream()
                .map(Category::getTag)
                .collect(Collectors.joining(", "));
    }

    public void setTagsFormatted(String tagsFormatted) {
        this.tags = Arrays.stream(tagsFormatted.split(","))
                .map(String::trim)
                .map(tag -> new Category(tag, this)) // this로 현재 Post와 연결
                .collect(Collectors.toList());
    }
}
