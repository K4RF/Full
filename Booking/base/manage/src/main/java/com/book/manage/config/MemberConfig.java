package com.book.manage.config;

import com.book.manage.repository.member.MemberJpaRepository;
import com.book.manage.repository.member.MemberRepository;
import com.book.manage.service.member.MemberJpaService;
import com.book.manage.service.member.MemberService;
import com.querydsl.core.annotations.Config;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;

@Configuration
@RequiredArgsConstructor
public class MemberConfig {
    private final EntityManager em;

    @Bean
    public MemberRepository memberRepository(){
        return new MemberJpaRepository(em);
    }

    @Bean(name = "memberService")
    public MemberService memberService() {
        return new MemberJpaService(memberRepository());
    }
}
