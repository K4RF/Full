package com.book.manage.service.order;

import com.book.manage.entity.Book;
import com.book.manage.entity.Member;
import com.book.manage.entity.Order;
import com.book.manage.repository.book.BookRepository;
import com.book.manage.repository.member.MemberRepository;
import com.book.manage.repository.order.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
class OrderServiceImplTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BookRepository bookRepository;

    private Member defaultMember;
    private Book defaultBook;
    private static int quantity = 2;

    @BeforeEach
    void setUp() {
        // 회원 기본 데이터 생성
        defaultMember = new Member();
        defaultMember.setLoginId("testUser");
        defaultMember.setPassword("password123");
        defaultMember.setNickname("Test Nickname");
        defaultMember = memberRepository.save(defaultMember);

        // 도서 기본 데이터 생성
        defaultBook = new Book();
        defaultBook.setTitle("Test Book");
        defaultBook.setAuthor("Author Name");
        defaultBook.setPublisher("Publisher Name");
        defaultBook.setDetails("Sample details for testing.");
        defaultBook.setPublishDate(LocalDate.now());
        defaultBook = bookRepository.save(defaultBook);

        log.info("Created default member: {}", defaultMember);
        log.info("Created default book: {}", defaultBook);
    }

    @Test
    void createOrder_shouldSaveOrderSuccessfully() {
        // Given
        double bookPrice = 29.99;

        log.info("Creating order for member {} and book {}", defaultMember.getMemberId(), defaultBook.getBookId());

        // When
        Order createdOrder = orderService.createOrder(defaultMember.getMemberId(), defaultBook.getBookId(), quantity);

        // Then
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getBook().getBookId()).isEqualTo(defaultBook.getBookId());
        assertThat(createdOrder.getMember().getMemberId()).isEqualTo(defaultMember.getMemberId());
        assertThat(createdOrder.getBookPrice()).isEqualTo(bookPrice);
        assertThat(createdOrder.getOrderDate()).isEqualTo(LocalDate.now());

        log.info("Created order: {}", createdOrder);
    }

    @Test
    void getOrdersByMember_shouldReturnOrdersForMember() {
        // Given
        double bookPrice = 19.99;
        orderService.createOrder(defaultMember.getMemberId(), defaultBook.getBookId(), quantity);

        log.info("Fetching orders for member {}", defaultMember.getMemberId());

        // When
        List<Order> orders = orderService.getOrdersByMember(defaultMember.getMemberId());

        // Then
        assertThat(orders).isNotEmpty();
        assertThat(orders).allMatch(order -> order.getMember().getMemberId().equals(defaultMember.getMemberId()));

        log.info("Orders for member: {}", orders);
    }

    @Test
    void getOrdersByBook_shouldReturnOrdersForBook() {
        // Given
        double bookPrice = 39.99;
        orderService.createOrder(defaultMember.getMemberId(), defaultBook.getBookId(), quantity);

        log.info("Fetching orders for book {}", defaultBook.getBookId());

        // When
        List<Order> orders = orderService.getOrdersByBook(defaultBook.getBookId());

        // Then
        assertThat(orders).isNotEmpty();
        assertThat(orders).allMatch(order -> order.getBook().getBookId().equals(defaultBook.getBookId()));

        log.info("Orders for book: {}", orders);
    }

    @Test
    void cancelOrder_shouldDeleteOrderSuccessfully() {
        // Given
        double bookPrice = 49.99;
        Order createdOrder = orderService.createOrder(defaultMember.getMemberId(), defaultBook.getBookId(), quantity);
        Long orderId = createdOrder.getOrderId();

        log.info("Cancelling order with ID {}", orderId);

        // When
        orderService.cancelOrder(orderId);

        // Then
        Optional<Order> cancelledOrder = orderRepository.findById(orderId);
        assertThat(cancelledOrder).isEmpty();

        log.info("Order with ID {} cancelled successfully", orderId);
    }

    @Test
    void getOrdersByDateRange_shouldReturnOrdersWithinDateRange() {
        // Given
        double bookPrice = 15.99;
        orderService.createOrder(defaultMember.getMemberId(), defaultBook.getBookId(), quantity);

        log.info("Fetching orders within date range");

        // When
        List<Order> orders = orderService.getOrdersByDateRange(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));

        // Then
        assertThat(orders).isNotEmpty();
        assertThat(orders).allMatch(order -> order.getOrderDate().isAfter(LocalDate.now().minusDays(2))
                && order.getOrderDate().isBefore(LocalDate.now().plusDays(2)));

        log.info("Orders within date range: {}", orders);
    }
}
