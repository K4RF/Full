package com.book.manage.repository.order;

import com.book.manage.entity.Order;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.book.manage.entity.QOrder.order;

@Repository
public class OrderJpaRepository implements OrderRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;

    public OrderJpaRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public Order save(Order order) {
        if (order.getOrderId() == null) {
            em.persist(order);
            return order;
        } else {
            return em.merge(order);
        }
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        Order foundOrder = em.find(Order.class, orderId);
        return Optional.ofNullable(foundOrder);
    }

    @Override
    public List<Order> findByMemberId(Long memberId) {
        return query.selectFrom(order)
                .where(order.member.memberId.eq(memberId))
                .fetch();
    }

    @Override
    public List<Order> findByBookId(Long bookId) {
        return query.selectFrom(order)
                .where(order.book.bookId.eq(bookId))
                .fetch();
    }

    @Override
    public List<Order> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return query.selectFrom(order)
                .where(orderDateBetween(startDate, endDate))
                .fetch();
    }

    @Override
    public List<Order> findAll() {
        return query.selectFrom(order)
                .fetch();
    }

    @Override
    public void delete(Order order) {
        em.remove(order);
    }

    // 날짜 범위 조건
    private BooleanExpression orderDateBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return order.orderDate.between(startDate, endDate);
        } else if (startDate != null) {
            return order.orderDate.goe(startDate);
        } else if (endDate != null) {
            return order.orderDate.loe(endDate);
        }
        return null; // 조건이 없으면 null 반환
    }
}