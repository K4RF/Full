package com.book.manage.config;

import com.book.manage.repository.book.BookJpaRepository;
import com.book.manage.repository.book.BookRepository;
import com.book.manage.repository.book.RentalJpaRepository;
import com.book.manage.repository.book.RentalRepository;
import com.book.manage.repository.member.MemberJpaRepository;
import com.book.manage.repository.member.MemberRepository;

import com.book.manage.service.book.BookService;
import com.book.manage.service.book.BookServiceImpl;
import com.book.manage.service.book.RentalService;
import com.book.manage.service.book.RentalServiceImpl;
import com.book.manage.service.member.MemberService;
import com.book.manage.service.member.MemberServiceImpl;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TotalConfig {

    private final EntityManager em;
    @Bean
    public MemberRepository memberRepository() {
        return new MemberJpaRepository(em);
    }

    @Bean(name = "memberService")
    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public BookRepository bookRepository() {
        return new BookJpaRepository(em);
    }

    @Bean
    public BookService bookService() {
        return new BookServiceImpl(bookRepository());
    }

    @Bean
    public RentalRepository rentalRepository(){
        return new RentalJpaRepository(em);
    }

    @Bean
    public RentalService rentalService() {
        return new RentalServiceImpl(rentalRepository(), bookRepository(), memberRepository());
    }
}
