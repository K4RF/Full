package com.book.manage.service.member;

import com.book.manage.entity.Member;
import com.book.manage.entity.dto.MemberEditDto;
import com.book.manage.repository.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@Primary
@Transactional
@Slf4j
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
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

    // deleteMember 메서드 수정
    @Override
    public void deleteMember(Long memberId) {
        Optional<Member> member = memberRepository.findById(memberId);
        if (member.isPresent()) {
            // 삭제 로직
            memberRepository.deleteById(memberId);  // deleteById() 메서드를 호출하여 실제 삭제
            log.info("Successfully deleted member with ID: {} and updated their posts to 'deleteUser'", memberId);
        } else {
            log.warn("Failed to delete member. No member found with ID: {}", memberId);
        }
    }

}
