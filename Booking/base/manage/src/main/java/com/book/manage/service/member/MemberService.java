package com.book.manage.service.member;

import com.book.manage.entity.Member;

import java.util.Optional;

public interface MemberService {
    void join(Member member);

    Optional<Member> findMember(Long memberId);

    boolean isNameDuplicate(String nickname);

    void deleteMember(Long memberId);
}
