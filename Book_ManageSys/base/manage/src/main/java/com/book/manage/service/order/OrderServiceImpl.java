package com.book.manage.service.order;

import com.book.manage.entity.Book;
import com.book.manage.entity.Member;
import com.book.manage.entity.Order;
import com.book.manage.repository.book.BookRepository;
import com.book.manage.repository.member.MemberRepository;
import com.book.manage.repository.order.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    @Override
    public Order createOrder(Long memberId, Long bookId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("해당 회원을 찾을 수 없습니다."));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("해당 도서를 찾을 수 없습니다."));

        Order order = new Order(member, book, LocalDate.now());
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByMember(Long memberId) {
        return orderRepository.findByMemberId(memberId);
    }

    @Override
    public List<Order> getOrdersByBook(Long bookId) {
        return orderRepository.findByBookId(bookId);
    }

    @Override
    public List<Order> getOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        return orderRepository.findByDateRange(startDate, endDate);
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("해당 주문을 찾을 수 없습니다."));
        orderRepository.delete(order);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("해당 주문을 찾을 수 없습니다."));
    }
}