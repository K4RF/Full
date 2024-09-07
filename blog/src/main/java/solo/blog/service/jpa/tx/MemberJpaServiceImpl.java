package solo.blog.service.jpa.tx;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import solo.blog.entity.database.tx.Member;
import solo.blog.model.MemberUpdateDto;
import solo.blog.repository.jpa.tx.MemberJpaRepository;

import java.util.NoSuchElementException;
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

    @Override
    @Transactional
    public void updateMember(MemberUpdateDto updateDto) {
        if (updateDto.getMemberId() == null) {
            throw new IllegalArgumentException("회원 ID가 존재하지 않습니다.");
        }

        Optional<Member> memberOptional = memberJpaRepository.findById(updateDto.getMemberId());
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            member.setName(updateDto.getName());
            if (updateDto.getPassword() != null && !updateDto.getPassword().isEmpty()) {
                member.setPassword(updateDto.getPassword());
            }
            memberJpaRepository.save(member);  // 변경된 내용 저장
        } else {
            throw new NoSuchElementException("해당 ID를 가진 회원을 찾을 수 없습니다.");
        }
    }
}
