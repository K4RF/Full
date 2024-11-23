package com.book.manage.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;

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

    @Column(nullable = false, length = 30)
    private String category;
}
