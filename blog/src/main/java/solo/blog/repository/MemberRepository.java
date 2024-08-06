package solo.blog.repository;

import solo.blog.entity.Member;

public interface MemberRepository {
    void save(Member member);

    Member findById(Long memberId);
}
