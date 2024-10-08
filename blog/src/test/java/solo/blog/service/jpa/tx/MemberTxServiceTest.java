package solo.blog.service.jpa.tx;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;
import solo.blog.entity.database.tx.Member;
import solo.blog.repository.jpa.tx.LogRepository;
import solo.blog.repository.jpa.tx.MemberTxRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberTxServiceTest {
    @Autowired
    MemberTxService memberService;
    @Autowired
    MemberTxRepository memberRepository;
    @Autowired
    LogRepository logRepository;

    /**
     * memberService    @Transactional:OFF
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON
     */
    @Test
    void outerTxOff_success() {
        //given
        Member member = new Member("로그인", "outerTxOff_success", "비밀번호");
        String name = member.getName();

        //when
        memberService.joinV1(member);

        //then: 모든 데이터가 정상 저장된다.
        assertTrue(memberRepository.find(name).isPresent());
        assertTrue(logRepository.find(name).isPresent());
    }

    /**
     * memberService    @Transactional:OFF
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON Exception
     */
    @Test
    void outerTxOff_fail() {
        //given
        Member member = new Member("로그인", "로그예외_outerTxOff_fail", "비밀번호");
        String name = member.getName();

        //when
        assertThatThrownBy(() -> memberService.joinV1(member))
                .isInstanceOf(RuntimeException.class);

        //when: log 데이터는 롤백된다.
        assertTrue(memberRepository.find(name).isPresent());
        assertTrue(logRepository.find(name).isEmpty());
    }

    /**
     * memberService    @Transactional:ON
     * memberRepository @Transactional:OFF
     * logRepository    @Transactional:OFF
     */
    @Test
    void singleTx() {
        //given
        Member member = new Member("로그인", "singleTx", "비밀번호");
        String name = member.getName();

        //when
        memberService.joinV1(member);

        //when: 모든 데이터가 정상 저장된다.
        assertTrue(memberRepository.find(name).isPresent());
        assertTrue(logRepository.find(name).isPresent());
    }

    /**
     * memberService    @Transactional:ON
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON
     */
    @Test
    void outerTxOn_success() {
        //given
        Member member = new Member("로그인", "outerTxOn_success", "비밀번호");
        String name = member.getName();

        //when
        memberService.joinV1(member);

        //when: 모든 데이터가 정상 저장된다.
        assertTrue(memberRepository.find(name).isPresent());
        assertTrue(logRepository.find(name).isPresent());
    }

    /**
     * memberService    @Transactional:ON
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON Exception
     */
    @Test
    void outerTxOn_fail() {
        //given
        Member member = new Member("로그인", "로그예외_outerTxOn_fail", "비밀번호");
        String name = member.getName();

        //when
        assertThatThrownBy(() -> memberService.joinV1(member))
                .isInstanceOf(RuntimeException.class);

        //when: 모든 데이터가 롤백된다.
        assertTrue(memberRepository.find(name).isEmpty());
        assertTrue(logRepository.find(name).isEmpty());
    }

    /**
     * memberService    @Transactional:ON
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON Exception
     */
    @Test
    void recoverException_fail() {
        //given
        Member member = new Member("로그인", "로그예외_recoverException_fail", "비밀번호");
        String name = member.getName();

        //when
        assertThatThrownBy(() -> memberService.joinV2(member))
                .isInstanceOf(UnexpectedRollbackException.class);

        //when: 모든 데이터가 롤백된다.
        assertTrue(memberRepository.find(name).isEmpty());
        assertTrue(logRepository.find(name).isEmpty());
    }

    /**
     * memberService    @Transactional:ON
     * memberRepository @Transactional:ON
     * logRepository    @Transactional:ON(REQUIRES_NEW) Exception
     */
    @Test
    void recoverException_success() {
        //given
        Member member = new Member("로그인", "로그예외_recoverException_success", "비밀번호");
        String name = member.getName();

        //when
        memberService.joinV2(member);

        //when: member 저장, log 롤백
        assertTrue(memberRepository.find(name).isPresent());
        assertTrue(logRepository.find(name).isEmpty());
    }
}