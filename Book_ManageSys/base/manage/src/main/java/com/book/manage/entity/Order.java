package com.book.manage.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "book_price", nullable = false)
    private int bookPrice;

    @Column(name = "order_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate orderDate;

    public Order() {
    }

    public Order(Member member, Book book, LocalDate orderDate) {
        this.member = member;
        this.book = book;
        this.bookPrice = book.getPrice(); // 도서 가격을 Book 엔티티에서 가져옴
        this.orderDate = orderDate;
    }
}
