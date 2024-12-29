package com.book.manage.repository.order;

import com.book.manage.entity.Order;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order); // 주문 저장
    Optional<Order> findById(Long orderId); // 주문 ID로 조회
    List<Order> findByMemberId(Long memberId); // 회원 ID로 주문 목록 조회
    List<Order> findByBookId(Long bookId); // 도서 ID로 주문 목록 조회
    List<Order> findByDateRange(LocalDate startDate, LocalDate endDate); // 날짜 범위로 조회
    List<Order> findAll(); // 전체 주문 조회
    void delete(Order order); // 주문 삭제
}