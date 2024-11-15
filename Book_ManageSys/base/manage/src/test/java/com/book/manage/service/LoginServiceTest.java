package com.book.manage.service;

import com.book.manage.entity.Member;
import com.book.manage.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LoginServiceTest {
    @Autowired
    private LoginService loginService;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp(){
        //테스트를 위한 멤버 저장
        Member member = new Member("testUser", "TestName", "password");
        memberRepository.save(member);
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