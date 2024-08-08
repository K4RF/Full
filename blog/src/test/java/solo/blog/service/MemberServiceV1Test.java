package solo.blog.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import solo.blog.config.AppConfig;
import solo.blog.entity.v1.Member;
import solo.blog.entity.priory.Priory;
import solo.blog.service.v1.MemberServiceV1;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class MemberServiceV1Test {
    MemberServiceV1 memberServiceV1;
    @BeforeEach
    public void beforeEach(){
        AppConfig appConfig = new AppConfig();
        memberServiceV1 = appConfig.memberService();
    }

    @Test
    void join() {
        // given
        Member member = new Member("memberA", Priory.USUAL);

        // when
        memberServiceV1.join(member);
        Member findMember = memberServiceV1.findMember(1L);

        // log
        log.info("findMember={}", findMember.getName());

        // then
        assertThat(member).isEqualTo(findMember);
    }
}