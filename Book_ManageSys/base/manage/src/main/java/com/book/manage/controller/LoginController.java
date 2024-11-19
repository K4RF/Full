package com.book.manage.controller;

import com.book.manage.login.session.SessionConst;
import com.book.manage.entity.form.LoginForm;
import com.book.manage.entity.Member;
import com.book.manage.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(
            @ModelAttribute("loginForm") LoginForm form,
            @RequestParam(required = false) String redirectURL,
            HttpServletRequest request) {
        // 로그인된 사용자인지 확인
        HttpSession session = request.getSession(false);
        Member loginMember = (session != null) ? (Member) session.getAttribute(SessionConst.LOGIN_MEMBER) : null;

        if (loginMember != null) {
            // 이미 로그인된 경우 홈으로 리다이렉트
            return "redirect:/";
        }

        // redirectURL 설정 및 세션에 저장
        if (redirectURL == null || redirectURL.isBlank()) {
            redirectURL = request.getHeader("Referer");
        }
        HttpSession newSession = request.getSession();
        newSession.setAttribute("redirectURL", redirectURL);
        
        return "login/loginForm";
    }

    @PostMapping("/login")
    public String loginFilter(
            @Valid @ModelAttribute LoginForm form,
            BindingResult bindingResult,
            HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member member = loginService.login(form.getLoginId(), form.getPassword());

        if (member == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다");
            return "login/loginForm";
        }

        // 세션에 로그인 정보 저장
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, member);

        // 로그인 성공 후 원래 URL로 리다이렉트
        String redirectURL = (String) session.getAttribute("redirectURL");
        session.removeAttribute("redirectURL"); // 세션에서 값 제거

        return "redirect:" + (redirectURL != null ? redirectURL : "/");
    }

    @PostMapping("/logout")
    public String logoutServlet(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // 세션 무효화
        }

        // 로그아웃 후 이전 페이지로 리다이렉트 (없으면 홈으로 이동)
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @GetMapping("/login/cancel")
    public String cancelLogin(HttpServletRequest request) {
        // 세션에서 redirectURL 가져오기
        HttpSession session = request.getSession(false);
        String redirectURL = (session != null) ? (String) session.getAttribute("redirectURL") : null;

        // 리다이렉트 대상 URL 설정
        String targetURL = (redirectURL != null && !redirectURL.isBlank()) ? redirectURL : "/";

        return "redirect:" + targetURL;
    }
}
