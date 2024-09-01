package solo.blog.service.jpa.tx;

import solo.blog.entity.database.tx.Member;
import solo.blog.repository.jpa.tx.MemberJpaRepository;

import java.util.Optional;


public class MemberJpaServiceImpl implements MemberJpaService{
    private final MemberJpaRepository memberJpaRepository;

    public MemberJpaServiceImpl(MemberJpaRepository memberJpaRepository) {
        this.memberJpaRepository = memberJpaRepository;
    }

    @Override
    public void join(Member member) {
        memberJpaRepository.save(member);
    }

    @Override
    public Optional<Member> findMember(Long memberId) {
        return memberJpaRepository.findById(memberId);
    }
}
