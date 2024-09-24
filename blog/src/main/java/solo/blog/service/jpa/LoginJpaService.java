package solo.blog.service.jpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import solo.blog.entity.database.tx.Member;
import solo.blog.repository.jpa.tx.MemberJpaRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginJpaService {
    private final MemberJpaRepository memberJpaRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인 시도
     *
     * @param loginId  로그인 ID
     * @param password 비밀번호
     * @return 로그인 성공 시 Member 객체, 실패 시 null
     */
    public Member login(String loginId, String password) {
        try {
            return memberJpaRepository.findByLoginId(loginId)
                    .filter(m -> m.getPassword().equals(password))
                    .orElse(null);
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            throw e;
        }
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}