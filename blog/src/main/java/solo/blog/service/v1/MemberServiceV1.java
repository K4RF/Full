package solo.blog.service.v1;

import solo.blog.entity.v1.Member;

public interface MemberServiceV1 {
    void join(Member member);

    Member findMember(Long memberId);
}
