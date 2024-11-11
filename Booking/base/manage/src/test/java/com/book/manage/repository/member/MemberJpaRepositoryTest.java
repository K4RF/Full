package com.book.manage.repository.member;

import com.book.manage.entity.Member;
import com.book.manage.service.member.MemberJpaService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {
    @Autowired
    private MemberRepository memberJpaRepository;

    @Autowired
    private MemberJpaService memberJpaService;

    @BeforeEach
    void setUp(){
        // 테스트 데이터를 위한 멤버 초기화 및 저장
        Member member = new Member("testUser", "Test Name", "password");
        memberJpaRepository.save(member);
    }

    @Test
    void save(){
        //given
        Member member = new Member("아이디", "이름", "비밀번호");
        // when
        Member savedMember = memberJpaRepository.save(member);

        //then
        Member findMember = memberJpaRepository.findById(savedMember.getMemberId()).orElse(null);
        assertThat(findMember).isNotNull();
        assertThat(findMember).isEqualTo(savedMember);
    }

}