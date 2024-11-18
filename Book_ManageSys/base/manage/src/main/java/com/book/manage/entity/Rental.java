package com.book.manage.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "rental")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long rentalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Book book;

    @Column(name = "rental_status", nullable = false, length = 20)
    private String rentalStatus;

    @Column(name = "retnal_date", nullable = false)
    private LocalDate rentalDate;

    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;
}
