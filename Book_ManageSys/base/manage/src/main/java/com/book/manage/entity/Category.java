package com.book.manage.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tag", nullable = false)
    private String tag;

    @ManyToMany(mappedBy = "categories")
    @ToString.Exclude
    private Set<Book> books = new HashSet<Book>();

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    public Category() {}
    public Category(String tag) {
        this.tag = tag;
    }
    public Category(String tag, Book book) {
        this.tag = tag;
        this.book = book;
    }
}
