package solo.blog.service.v2;


import org.springframework.stereotype.Service;
import solo.blog.entity.v2.Member;
import solo.blog.repository.v2.MemberRepositoryV2;

@Service
public class MemberServiceV2 {
    private final MemberRepositoryV2 memberRepositoryV2;

    public MemberServiceV2(MemberRepositoryV2 memberRepositoryV2) {
        this.memberRepositoryV2 = memberRepositoryV2;
    }

    public void join(Member member) {
        memberRepositoryV2.save(member);
    }

    public Member findMember(Long memberId) {
        return memberRepositoryV2.findById(memberId);
    }
}
