package solo.blog.service.jpa;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import solo.blog.entity.database.tx.Member;
import solo.blog.repository.jpa.tx.MemberJpaRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class LoginServiceTest {

    @Autowired
    private LoginJpaService loginService;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @BeforeEach
    void setUp() {
        // 테스트를 위한 멤버 저장
        Member member = new Member("testUser", "Test Name", "password");
        memberJpaRepository.save(member);
    }

    @Test
    void login_shouldReturnMemberWhenCredentialsAreCorrect() {
        // Given
        String loginId = "testUser";
        String password = "password";

        // When
        Member result = loginService.login(loginId, password);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLoginId()).isEqualTo(loginId);
    }

    @Test
    void login_shouldReturnNullWhenPasswordIsIncorrect() {
        // Given
        String loginId = "testUser";
        String wrongPassword = "wrongPassword";

        // When
        Member result = loginService.login(loginId, wrongPassword);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void login_shouldReturnNullWhenLoginIdDoesNotExist() {
        // Given
        String nonExistentLoginId = "nonExistentUser";
        String password = "password";

        // When
        Member result = loginService.login(nonExistentLoginId, password);

        // Then
        assertThat(result).isNull();
    }
}
