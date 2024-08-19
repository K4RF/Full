package solo.blog.repository.jdbc;

import solo.blog.entity.database.Member;

public interface MemberRepository {
    Member save(Member member);

    Member findById(String memberId);

    void update(String memberId, String loginId, String name, String password);

    void delete(String memberId);
}
