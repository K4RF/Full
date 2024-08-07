package solo.blog.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import solo.blog.config.AppConfig;
import solo.blog.entity.Member;
import solo.blog.priory.Priory;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class MemberServiceTest {
    MemberService memberService;
    @BeforeEach
    public void beforeEach(){
        AppConfig appConfig = new AppConfig();
        memberService = appConfig.memberService();
    }

    @Test
    void join() {
        // given
        Member member = new Member("memberA", Priory.USUAL);

        // when
        memberService.join(member);
        Member findMember = memberService.findMember(1L);

        // log
        log.info("findMember={}", findMember.getName());

        // then
        assertThat(member).isEqualTo(findMember);
    }
}