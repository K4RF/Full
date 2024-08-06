package solo.blog.service;

import solo.blog.entity.Member;

public interface MemberService {
    void join(Member member);

    Member findMember(Long memberId);
}
