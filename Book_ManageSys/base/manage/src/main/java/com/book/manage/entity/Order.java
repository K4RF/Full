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
    private int bookPrice; // 단일 책의 가격

    @Column(name = "quantity", nullable = false)
    private int quantity; // 주문 수량

    @Column(name = "total_price", nullable = false)
    private int totalPrice; // 총 가격 (bookPrice * quantity)

    @Column(name = "order_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate orderDate;

    public Order() {
    }

    public Order(Member member, Book book,  LocalDate orderDate, int quantity) {
        this.member = member;
        this.book = book;
        this.bookPrice = book.getPrice(); // 도서 가격
        this.quantity = quantity; // 주문 수량
        this.totalPrice = calculateTotalPrice(); // 총 가격 계산
        this.orderDate = orderDate;
    }

    // 총 가격 계산 메서드
    private int calculateTotalPrice() {
        return this.bookPrice * this.quantity;
    }
}
