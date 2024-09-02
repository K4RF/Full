package solo.blog.service.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solo.blog.entity.database.tx.Member;
import solo.blog.repository.jpa.tx.MemberJpaRepository;

@Service
@RequiredArgsConstructor
public class LoginJpaService {
    private final MemberJpaRepository memberJpaRepository;

    /**
     * 로그인 시도
     *
     * @param loginId  로그인 ID
     * @param password 비밀번호
     * @return 로그인 성공 시 Member 객체, 실패 시 null
     */
    public Member login(String loginId, String password) {
        return memberJpaRepository.findByLoginId(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }
}