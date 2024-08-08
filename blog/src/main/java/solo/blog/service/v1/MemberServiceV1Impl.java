package solo.blog.service.v1;

import solo.blog.entity.v1.Member;
import solo.blog.repository.v1.MemberRepositoryV1;

public class MemberServiceV1Impl implements MemberServiceV1 {
    private final MemberRepositoryV1 memberRepositoryV1;

    public MemberServiceV1Impl(MemberRepositoryV1 memberRepositoryV1) {
        this.memberRepositoryV1 = memberRepositoryV1;
    }

    @Override
    public void join(Member member) {
        memberRepositoryV1.save(member);
    }

    @Override
    public Member findMember(Long memberId) {
        return memberRepositoryV1.findById(memberId);
    }
}
