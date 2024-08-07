package solo.blog.repository.v1;

import solo.blog.entity.v1.Member;

import java.util.List;

public interface MemberRepositoryV1 {
    Member save(Member member);

    Member findById(Long memberId);

    List<Member> findAll();

    void clearStore();
}
