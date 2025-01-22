package com.book.manage.controller;

import com.book.manage.entity.Member;
import com.book.manage.entity.dto.KakaoUserInfoResponseDto;
import com.book.manage.login.session.SessionConst;
import com.book.manage.service.login.KakaoLoginService;
import com.book.manage.service.member.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;
    private final MemberService memberService;

    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code, HttpServletRequest request) {
        // 1. Access Token 가져오기
        String accessToken = kakaoLoginService.getAccessTokenFromKakao(code);

        // 2. 사용자 정보 가져오기
        KakaoUserInfoResponseDto userInfo = kakaoLoginService.getUserInfo(accessToken);

        // 3. 사용자 정보를 기반으로 회원 조회 또는 생성
        Member member = memberService.findOrCreateMember(
                userInfo.getId(), // 카카오 고유 ID
                userInfo.getKakaoAccount().getProfile().getNickName()
        );

        // 4. 세션에 사용자 정보 저장
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, member);

        return new ResponseEntity<>("로그인 성공", HttpStatus.OK);
    }
}
