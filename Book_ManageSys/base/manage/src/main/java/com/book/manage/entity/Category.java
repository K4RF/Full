package com.book.manage.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "category")
@EqualsAndHashCode(exclude = "books")  // books 필드를 제외하여 무한 참조 방지
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cate", nullable = false)
    private String cate;

    @ManyToMany(mappedBy = "categories")
    @ToString.Exclude
    private Set<Book> books = new HashSet<Book>();

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    public Category() {}

    public Category(String cate) {
        this.cate = cate;
    }

    public Category(String cate, Book book) {
        this.cate = cate;
        this.book = book;
    }
}
