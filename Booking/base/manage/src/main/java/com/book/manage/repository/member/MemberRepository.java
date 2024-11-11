package com.book.manage.repository.member;

import com.book.manage.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);

    Optional<Member> findById(Long id);

    Optional<Member> findByLoginId(String loginId);

    List<Member> findAll();

    boolean existsByName(String name);

    Optional<Member> findByName(String name);

    void deleteById(Long id);
}
