package com.book.manage.service.member;

import com.book.manage.entity.Member;
import com.book.manage.entity.dto.MemberEditDto;
import com.book.manage.repository.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
@Primary
@Transactional
@Slf4j
public class MemberJpaService implements MemberService{
    private final MemberRepository memberRepository;

    public MemberJpaService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Optional<Member> findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }

    @Override
    @Transactional
    public Member editMember(MemberEditDto editDto) {
        Member member = memberRepository.findById(editDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원 ID가 존재하지 않습니다."));

        // 이름이 비어 있지 않을 경우에만 업데이트
        if (StringUtils.hasText(editDto.getNickname())) {
            member.setNickname(editDto.getNickname());
        }
        // 비밀번호가 비어 있지 않을 경우에만 해시화하여 업데이트
        if (StringUtils.hasText(editDto.getPassword())) {
            member.setPassword(editDto.getPassword());
        }

        return memberRepository.save(member);
    }


    @Override
    public boolean isNameDuplicate(String nickname) {
        return memberRepository.existsByName(nickname);
    }

    @Override
    public void deleteMember(Long memberId) {
        Optional<Member> member = memberRepository.findById(memberId);
        if (member.isPresent()) {
            // 해당 회원의 모든 글 및 대출 상황 데이터 삭제 코드 추가 필요

            // 회원 삭제
            memberRepository.deleteById(memberId);
            log.info("성공적으로 제거됨: {}", memberId);
        }else {
            log.info("Failed to delete Member. No member found with ID: {}", memberId);
        }
    }
}
