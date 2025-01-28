package com.book.manage.config;

import com.book.manage.repository.book.BookJpaRepository;
import com.book.manage.repository.book.BookRepository;
import com.book.manage.repository.comment.CommentJpaRepository;
import com.book.manage.repository.comment.CommentRepository;
import com.book.manage.repository.order.OrderJpaRepository;
import com.book.manage.repository.order.OrderRepository;
import com.book.manage.repository.rental.RentalJpaRepository;
import com.book.manage.repository.rental.RentalRepository;
import com.book.manage.repository.category.CategoryRepository;
import com.book.manage.repository.category.CategoryJpaRepository;
import com.book.manage.repository.member.MemberJpaRepository;
import com.book.manage.repository.member.MemberRepository;

import com.book.manage.service.book.BookService;
import com.book.manage.service.book.BookServiceImpl;
import com.book.manage.service.comment.CommentService;
import com.book.manage.service.comment.CommentServiceImpl;
import com.book.manage.service.order.OrderService;
import com.book.manage.service.order.OrderServiceImpl;
import com.book.manage.service.rental.RentalService;
import com.book.manage.service.rental.RentalServiceImpl;
import com.book.manage.service.category.CategoryService;
import com.book.manage.service.category.CategoryServiceImpl;
import com.book.manage.service.member.MemberService;
import com.book.manage.service.member.MemberServiceImpl;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class TotalConfig {

    private final EntityManager em;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public MemberRepository memberRepository() {
        return new MemberJpaRepository(em);
    }

    @Bean(name = "memberService")
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository(), passwordEncoder, commentService(), rentalRepository(), orderService());
    }

    @Bean
    public BookRepository bookRepository() {
        return new BookJpaRepository(em);
    }

    @Bean
    public BookService bookService() {
        return new BookServiceImpl(bookRepository(), categoryService(),categoryRepository(), commentService());
    }

    @Bean
    public RentalRepository rentalRepository(){
        return new RentalJpaRepository(em);
    }

    @Bean
    public RentalService rentalService() {
        return new RentalServiceImpl(rentalRepository(), bookRepository(), memberRepository());
    }
    @Bean
    public CategoryRepository categoryRepository() {
        return new CategoryJpaRepository(em);
    }

    @Bean
    public CategoryService categoryService() {
        return new CategoryServiceImpl(categoryRepository());
    }

    @Bean
    public CommentRepository commentRepository(){
        return new CommentJpaRepository(em);
    }

    @Bean
    public CommentService commentService(){
        return new CommentServiceImpl(commentRepository(), bookRepository());
    }

    @Bean
    public OrderRepository orderRepository(){
        return new OrderJpaRepository(em);
    }

    @Bean
    public OrderService orderService(){
        return new OrderServiceImpl(orderRepository(), memberRepository(), bookRepository());
    }
}
