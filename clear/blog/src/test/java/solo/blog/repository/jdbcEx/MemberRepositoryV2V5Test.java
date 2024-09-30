package solo.blog.repository.jdbcEx;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import solo.blog.repository.jdbc.ex.MemberRepository;
import solo.blog.repository.jdbc.ex.MemberRepositoryV5;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
@SpringBootTest
class MemberRepositoryV2V5Test {
    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";
    @Autowired
    MemberRepository memberRepositoryV5;

    @AfterEach
    void after() throws SQLException {
        memberRepositoryV5.delete(MEMBER_A);
        memberRepositoryV5.delete(MEMBER_B);
        memberRepositoryV5.delete(MEMBER_EX);
    }

    @TestConfiguration
    static class TestConfig {
        private final DataSource dataSource;

        public TestConfig(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Bean
        MemberRepository memberRepositoryV5() {
//return new MemberRepositoryV4_1(dataSource); //단순 예외 변환
//            return new MemberRepositoryV4_2(dataSource); //스프링 예외 변환
            return new MemberRepositoryV5(dataSource);
        }
    }

    @Test
    void AopCheck() {
        log.info("memberRepository class={}", memberRepositoryV5.getClass());
        Assertions.assertThat(AopUtils.isAopProxy(memberRepositoryV5)).isFalse();
    }
}