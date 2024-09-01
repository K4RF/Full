package solo.blog.repository.jpa.tx;



import solo.blog.entity.database.tx.Member;

import java.util.List;
import java.util.Optional;

public interface MemberJpaRepository {
    Member save(Member member);
    Optional<Member> findById(Long id);
    Optional<Member> findByLoginId(String loginId);
    List<Member> findAll();
}
