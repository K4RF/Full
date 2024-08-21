package solo.blog.service.v3;


import org.springframework.stereotype.Service;
import solo.blog.entity.v2.Member;
import solo.blog.repository.v2.MemberRepository;

@Service
public class MemberService{
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void join(Member member) {
        memberRepository.save(member);
    }

    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
