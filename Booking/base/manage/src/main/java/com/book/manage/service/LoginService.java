package com.book.manage.service;

import com.book.manage.entity.Member;
import com.book.manage.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;

    /**
     * 로그인 시도
     * param loginID 로그인 ID
     * param password 비밀번호
     * return 로그인 성공 시 Member 객체, 실패 시 null
     */

    public Member login(String loginId, String password) {
        return memberRepository.findByLoginId(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }
}
