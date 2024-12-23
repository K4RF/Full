package com.book.manage.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "rental")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rentalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "rental_status", nullable = false, length = 20)
    private String rentalStatus;

    @Column(name = "rental_date", nullable = false)
    private LocalDate rentalDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(nullable = false)
    private int extensionCount = 0; // 연장 횟수
}
