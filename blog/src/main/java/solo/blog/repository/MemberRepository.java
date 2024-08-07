package solo.blog.repository;

import solo.blog.entity.Member;

import java.util.List;

public interface MemberRepository {
    Member save(Member member);

    Member findById(Long memberId);

    List<Member> findAll();

    void clearStore();
}
