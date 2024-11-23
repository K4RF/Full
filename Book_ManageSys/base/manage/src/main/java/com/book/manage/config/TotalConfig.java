package com.book.manage.config;

import com.book.manage.repository.book.BookJpaRepository;
import com.book.manage.repository.book.BookRepository;
import com.book.manage.repository.book.RentalJpaRepository;
import com.book.manage.repository.book.RentalRepository;
import com.book.manage.repository.book.category.CategoryRepository;
import com.book.manage.repository.book.category.CategoryJpaRepository;
import com.book.manage.repository.member.MemberJpaRepository;
import com.book.manage.repository.member.MemberRepository;

import com.book.manage.service.book.BookService;
import com.book.manage.service.book.BookServiceImpl;
import com.book.manage.service.book.RentalService;
import com.book.manage.service.book.RentalServiceImpl;
import com.book.manage.service.book.category.CategoryService;
import com.book.manage.service.book.category.CategoryServiceImpl;
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
        return new MemberServiceImpl(memberRepository(),rentalRepository(), passwordEncoder);
    }

    @Bean
    public BookRepository bookRepository() {
        return new BookJpaRepository(em);
    }

    @Bean
    public BookService bookService() {
        return new BookServiceImpl(bookRepository(), categoryService());
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
}
