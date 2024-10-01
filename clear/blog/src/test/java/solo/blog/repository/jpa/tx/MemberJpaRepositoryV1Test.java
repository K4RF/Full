package solo.blog.repository.jpa.tx;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import solo.blog.entity.database.tx.Member;
import solo.blog.service.jpa.tx.MemberJpaServiceImpl;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
class MemberJpaRepositoryV1Test {
    @Autowired
    private MemberJpaRepositoryV1 memberJpaRepository;

    @Autowired
    private MemberJpaServiceImpl memberJpaService;

    @BeforeEach
    void setUp() {
        // 테스트 데이터를 위한 멤버 초기화 및 저장
        Member member = new Member("testUser", "Test Name", "password");
        memberJpaRepository.save(member);
    }

    @Test
    void save(){
        // given
        Member member = new Member("아이디", "이름", "비밀번호");

        //when
        Member savedMember = memberJpaRepository.save(member);

        // then
        Member findMember = memberJpaRepository.findById(savedMember.getId()).orElse(null);
        assertThat(findMember).isNotNull();
        assertThat(findMember).isEqualTo(savedMember);
    }

    @Test
    void testFindMember() {
        // given
        Member member = memberJpaRepository.findByLoginId("testUser").orElseThrow();
        Long memberId = member.getId();

        // when
        Optional<Member> foundMember = memberJpaService.findMember(memberId);

        // then
        assertTrue(foundMember.isPresent());
        assertEquals("testUser", foundMember.get().getLoginId());
        assertEquals("Test Name", foundMember.get().getName());
    }
}