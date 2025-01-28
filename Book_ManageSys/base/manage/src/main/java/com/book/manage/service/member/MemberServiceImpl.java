package com.book.manage.service.member;

import com.book.manage.entity.Comment;
import com.book.manage.entity.Member;
import com.book.manage.entity.Role;
import com.book.manage.entity.dto.MemberEditDto;
import com.book.manage.repository.member.MemberRepository;
import com.book.manage.service.comment.CommentService;
import com.book.manage.service.rental.RentalService;
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
    private final RentalService rentalService;
    private final PasswordEncoder passwordEncoder;

    private final CommentService commentService;

    public MemberServiceImpl(MemberRepository memberRepository, RentalService rentalService, PasswordEncoder passwordEncoder, CommentService commentService) {
        this.memberRepository = memberRepository;
        this.rentalService = rentalService;
        this.passwordEncoder = passwordEncoder;
        this.commentService = commentService;
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
        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다"));

        String nickname = member.getNickname(); // 댓글 삭제를 위해 회원의 닉네임 가져오기

        // 해당 회원의 대출 정보를 삭제
        rentalService.deleteRentalsByMemberId(memberId);

        // 해당 회원이 작성한 댓글 삭제
        List<Comment> comments = commentService.getCommentsByWriter(nickname);
        for (Comment comment : comments) {
            commentService.deleteComment(comment.getCommentId(), nickname);
        }

        // 최종적으로 회원 삭제
        memberRepository.deleteById(member.getMemberId());
    }

    @Override
    public Member findOrCreateMember(Long kakaoId, String nickname) {
        // kakaoId로 회원 검색
        Member member = memberRepository.findByKakaoId(kakaoId);

        // 기존 회원이 없으면 생성
        if (member == null) {
            member = new Member();
            member.setKakaoId(kakaoId);
            member.setNickname(nickname);
            // loginId는 kakaoId를 기준으로 생성하며 최대 16자로 제한
            String loginId = "kakao_" + kakaoId;
            member.setLoginId(loginId.substring(0, Math.min(16, loginId.length())));
            // default_password를 해시화하여 저장
            String encodedPassword = passwordEncoder.encode("default_password");
            member.setPassword(encodedPassword);

            member.setRole(Role.USER); // 기본 역할 설정
            memberRepository.save(member);
        }

        return member;
    }

    public List<Member> findAllMembers() {
        return memberRepository.findAll();
    }
}
