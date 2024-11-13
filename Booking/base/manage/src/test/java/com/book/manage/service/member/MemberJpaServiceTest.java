package com.book.manage.service.member;

import com.book.manage.entity.Member;
import com.book.manage.repository.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberJpaServiceTest {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    /**
     * memberService    @Transactional:OFF
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON
     */
    @Test
    void outerTxOff_success() {
        //given
        Member member = new Member("로그인", "outerTxOff_success", "password");
        String name = member.getNickname();

        //when
        memberService.join(member);

        //then: 모든 데이터가 정상 저장된다.
        assertTrue(memberRepository.findByName(name).isPresent());
    }

    /**
     * memberService    @Transactional:OFF
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON Exception
     */
    @Test
    void outerTxOff_fail() {
        //given
        Member member = new Member("로그인", "로그예외_outerTxOff_fail", "password");
        String name = member.getNickname();

        //when
        assertThatThrownBy(() -> memberService.join(member))
                .isInstanceOf(RuntimeException.class);

        //when: log 데이터는 롤백된다.
        assertTrue(memberRepository.findByName(name).isPresent());
    }

    /**
     * memberService    @Transactional:ON
     * memberRepository @Transactional:OFF
     * logRepository    @Transactional:OFF
     */
    @Test
    void singleTx() {
        //given
        Member member = new Member("로그인", "singleTx", "password");
        String name = member.getNickname();

        //when
        memberService.join(member);

        //when: 모든 데이터가 정상 저장된다.
        assertTrue(memberRepository.findByName(name).isPresent());
    }

    /**
     * memberService    @Transactional:ON
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON
     */
    @Test
    void outerTxOn_success() {
        //given
        Member member = new Member("로그인", "outerTxOn_success", "password");
        String name = member.getNickname();

        //when
        memberService.join(member);

        //when: 모든 데이터가 정상 저장된다.
        assertTrue(memberRepository.findByName(name).isPresent());
    }

    /**
     * memberService    @Transactional:ON
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON Exception
     */
    @Test
    void outerTxOn_fail() {
        //given
        Member member = new Member("로그인", "로그예외_outerTxOn_fail", "password");
        String name = member.getNickname();

        //when
        assertThatThrownBy(() -> memberService.join(member))
                .isInstanceOf(RuntimeException.class);

        //when: 모든 데이터가 롤백된다.
        assertTrue(memberRepository.findByName(name).isEmpty());
    }

    /**
     * memberService    @Transactional:ON
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON Exception
     */
    @Test
    void recoverException_fail() {
        //given
        Member member = new Member("로그인", "로그예외_recoverException_fail", "password");
        String name = member.getNickname();

        //when
        assertThatThrownBy(() -> memberService.join(member))
                .isInstanceOf(UnexpectedRollbackException.class);

        //when: 모든 데이터가 롤백된다.
        assertTrue(memberRepository.findByName(name).isEmpty());

    }

    /**
     * memberService    @Transactional:ON
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON(REQUIRES_NEW) Exception
     */
    @Test
    void recoverException_success() {
        //given
        Member member = new Member("로그인", "로그예외_recoverException_success", "password");
        String name = member.getNickname();

        //when
        memberService.join(member);

        //when: member 저장, log 롤백
        assertTrue(memberRepository.findByName(name).isPresent());
    }
}