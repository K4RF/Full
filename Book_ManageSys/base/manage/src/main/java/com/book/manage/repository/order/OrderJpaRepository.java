package com.book.manage.repository.order;

import com.book.manage.entity.Order;
import com.book.manage.entity.dto.OrderSearchDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import jakarta.transaction.Transactional;
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

    @Override
    public List<Order> findAll(OrderSearchDto searchParam) {
        String title = searchParam.getTitle();
        Long memberId = searchParam.getMemberId();  // memberId 추가

        return query
                .selectFrom(order)
                .where(
                        likeTitle(title)  // 제목 필터
                                .and(likeMemberId(memberId))  // 회원 ID 필터
                )
                .fetch();
    }

    // 제목 필터링
    private BooleanExpression likeTitle(String title) {
        if (title != null && !title.trim().isEmpty()) {
            return order.book.title.like("%" + title + "%");
        }
        return order.book.title.isNotNull(); // 제목이 없으면 모두 포함되도록 처리
    }

    // 회원 ID 필터링
    private BooleanExpression likeMemberId(Long memberId) {
        return memberId != null
                ? order.member.memberId.eq(memberId)  // 회원 ID가 일치하는 대출 기록만 조회
                : null;
    }

    @Override
    @Transactional
    public void deleteByMemberId(Long memberId) {
        // QueryDSL을 사용하여 삭제 쿼리 실행
        new JPADeleteClause(em, order)
                .where(order.member.memberId.eq(memberId))
                .execute();
    }
}