package solo.blog.service.jpa.tx;

import solo.blog.entity.database.tx.Member;

import java.util.Optional;

public interface MemberJpaService {
    void join(Member member);
    Optional<Member> findMember(Long memberId);

}
