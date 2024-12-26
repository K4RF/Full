package com.book.manage.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "orders") // 테이블 이름을 orders로 설정
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId; // 고유 주문 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 주문한 회원 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book; // 주문한 도서 정보

    @Column(name = "book_price", nullable = false)
    private double bookPrice; // 도서 가격

    @Column(name = "order_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate orderDate; // 주문 날짜

    public Order() {
        // 기본 생성자
    }

    public Order(Member member, Book book, double bookPrice, LocalDate orderDate) {
        this.member = member;
        this.book = book;
        this.bookPrice = bookPrice;
        this.orderDate = orderDate;
    }
}