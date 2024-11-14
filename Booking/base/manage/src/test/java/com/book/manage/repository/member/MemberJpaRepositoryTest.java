package com.book.manage.repository.member;

import com.book.manage.entity.Member;
import com.book.manage.service.member.MemberJpaService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


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
        Member member = new Member("saveT", "이름", "비밀번호1234");
        // when
        Member savedMember = memberJpaRepository.save(member);

        //then
        Member findMember = memberJpaRepository.findById(savedMember.getMemberId()).orElse(null);
        assertThat(findMember).isNotNull();
        assertThat(findMember).isEqualTo(savedMember);
    }


    @Test
    void testFindMember(){
//        given
        Member member = memberJpaRepository.findByLoginId("testUser").orElseThrow();
        Long memberId = member.getMemberId();
//        when
        Optional<Member> foundMember = memberJpaRepository.findById(memberId);
//        then
        assertTrue(foundMember.isPresent());
        assertEquals("testUser", foundMember.get().getLoginId());
        assertEquals("Test Name", foundMember.get().getNickname());
    }

    @Test
    public void deleteMember_shouldDeleteMember() {
        // Given
        Member member = new Member("deleteUser", "nickname", "password");
        memberJpaRepository.save(member);
        Long memberId = member.getMemberId();

        // When
        memberJpaService.deleteMember(memberId);

        // Then
        Optional<Member> deletedMember = memberJpaRepository.findById(memberId);
        assertThat(deletedMember).isEmpty();  // 삭제가 성공하면, findById는 비어 있어야 함
    }
    @Test
    @Commit// 트랜잭션 커밋을 강제하여 실제 데이터베이스 반영을 확인
    void deleteMember_shouldDeleteMemberCommit() {
        // Given
        Member member = new Member("deleteUser", "nickname", "password");
        memberJpaRepository.save(member);
        Long memberId = member.getMemberId();

        // When
        memberJpaService.deleteMember(memberId);

        // Then
        Optional<Member> deletedMember = memberJpaRepository.findById(memberId);
        assertThat(deletedMember).isEmpty();  // 삭제가 성공하면, findById는 비어 있어야 함
    }
}