package com.book.manage.service.member;

import com.book.manage.entity.Member;
import com.book.manage.entity.dto.MemberEditDto;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    void join(Member member);

    Optional<Member> findMember(Long memberId);
    Member editMember(MemberEditDto editDto);

    boolean isNameDuplicate(String nickname);

    void deleteMember(Long memberId);

    Member findOrCreateMember(Long kakaoId, String nickname);
    List<Member> findAllMembers();
}
