package com.book.manage.service.member;

import com.book.manage.entity.Book;
import com.book.manage.entity.Member;
import com.book.manage.entity.Rental;
import com.book.manage.entity.Role;
import com.book.manage.entity.dto.MemberEditDto;
import com.book.manage.repository.book.BookRepository;
import com.book.manage.repository.book.RentalRepository;
import com.book.manage.repository.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@Primary
@Transactional
@Slf4j
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    private final RentalRepository rentalRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberServiceImpl(MemberRepository memberRepository, RentalRepository rentalRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.rentalRepository = rentalRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void join(Member member) {
        if (member.getRole() == null) {
            member.setRole(Role.USER); // 기본값 설정
        }
        //비밀번호 해시화
        String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encodedPassword);
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
        // 비밀번호가 비어 있지 않을 경우에만 업데이트
        if (StringUtils.hasText(editDto.getPassword())) {
            String encodedPassword = passwordEncoder.encode(editDto.getPassword());
            member.setPassword(encodedPassword);
        }

        return memberRepository.save(member);
    }


    @Override
    public boolean isNameDuplicate(String nickname) {
        return memberRepository.existsByName(nickname);
    }

    // deleteMember 메서드 수정
    @Override
    @Transactional
    public void deleteMember(Long memberId) {
        // 해당 회원의 대출 정보를 삭제
        List<Rental> Rentals = rentalRepository.findByMemberMemberId(memberId);
        for (Rental Rental : Rentals) {
            rentalRepository.delete(Rental);
        }

        // 최종적으로 회원 삭제
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다"));
        memberRepository.deleteById(member.getMemberId());
    }

}
