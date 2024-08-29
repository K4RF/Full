package solo.blog.service.jpa.tx;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import solo.blog.entity.database.tx.Member;
import solo.blog.repository.jpa.tx.LogRepository;
import solo.blog.repository.jpa.tx.MemberTxRepository;

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
}