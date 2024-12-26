package com.book.manage.service.order;

import com.book.manage.entity.Order;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    Order createOrder(Long memberId, Long bookId, double bookPrice); // 주문 생성
    List<Order> getOrdersByMember(Long memberId); // 회원 ID로 주문 조회
    List<Order> getOrdersByBook(Long bookId); // 도서 ID로 주문 조회
    List<Order> getOrdersByDateRange(LocalDate startDate, LocalDate endDate); // 날짜 범위로 주문 조회
    void cancelOrder(Long orderId); // 주문 취소
    Order getOrderById(Long orderId);
}
