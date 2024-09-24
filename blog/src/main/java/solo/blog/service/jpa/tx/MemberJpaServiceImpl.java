package solo.blog.service.jpa.tx;

import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import solo.blog.entity.database.Post;
import solo.blog.entity.database.tx.Member;
import solo.blog.model.MemberUpdateDto;
import solo.blog.repository.jpa.tx.MemberJpaRepository;
import solo.blog.service.jpa.post.PostJpaService;

import java.util.List;
import java.util.Optional;


@Service
@Primary
@Transactional
public class MemberJpaServiceImpl implements MemberJpaService{
    private final MemberJpaRepository memberJpaRepository;
    private final PostJpaService postJpaService;
    private final PasswordEncoder passwordEncoder; // PasswordEncoder 주입

    public MemberJpaServiceImpl(MemberJpaRepository memberJpaRepository,
                                PostJpaService postJpaService,
                                PasswordEncoder passwordEncoder) {
        this.memberJpaRepository = memberJpaRepository;
        this.postJpaService = postJpaService;
        this.passwordEncoder = passwordEncoder; // 주입받은 PasswordEncoder 사용
    }



    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void join(Member member) {
        // 비밀번호 해시화
        String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encodedPassword);
        memberJpaRepository.save(member);
    }

    @Override
    public Optional<Member> findMember(Long memberId) {
        return memberJpaRepository.findById(memberId);
    }

    @Override
    @Transactional
    public Member updateMember(MemberUpdateDto memberUpdateDto) {
        Member member = memberJpaRepository.findById(memberUpdateDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("회원 ID가 존재하지 않습니다."));

        // 이름이 비어 있지 않을 경우에만 업데이트
        if (StringUtils.hasText(memberUpdateDto.getName())) {
            member.setName(memberUpdateDto.getName());
        }

        // 비밀번호가 비어 있지 않을 경우에만 해시화하여 업데이트
        if (StringUtils.hasText(memberUpdateDto.getPassword())) {
            String encodedPassword = passwordEncoder.encode(memberUpdateDto.getPassword());
            member.setPassword(encodedPassword);
        }

        // 게시글 작성자의 이름도 수정 (이름이 변경된 경우에만 업데이트)
        if (StringUtils.hasText(memberUpdateDto.getName())) {
            List<Post> postsByMember = postJpaService.findByLoginId(member.getLoginId());
            for (Post post : postsByMember) {
                post.setAuthorName(member.getName());
            }
        }

        return memberJpaRepository.save(member);
    }

    @Override
    public boolean isNameDuplicate(String name) {
        return memberJpaRepository.existsByName(name);
    }
}
