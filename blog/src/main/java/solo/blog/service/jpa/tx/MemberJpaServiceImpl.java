package solo.blog.service.jpa.tx;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import solo.blog.entity.database.tx.Member;
import solo.blog.repository.jpa.tx.MemberJpaRepository;

import java.util.Optional;


@Service
@Primary
@Transactional
public class MemberJpaServiceImpl implements MemberJpaService{
    private final MemberJpaRepository memberJpaRepository;

    public MemberJpaServiceImpl(MemberJpaRepository memberJpaRepository) {
        this.memberJpaRepository = memberJpaRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)

    @Override
    public void join(Member member) {
        memberJpaRepository.save(member);
    }

    @Override
    public Optional<Member> findMember(Long memberId) {
        return memberJpaRepository.findById(memberId);
    }
}
