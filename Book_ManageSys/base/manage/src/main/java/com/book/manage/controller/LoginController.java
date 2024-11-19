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
        Member loginMember = (Member) (session != null ? session.getAttribute(SessionConst.LOGIN_MEMBER) : null);

        /**
         * 취소 버튼 클릭 시에도 이전 URL로 리다이렉트 기능 추가
         */

        // redirectURL 설정: 파라미터 우선, 없으면 Referer
        if (redirectURL == null) {
            redirectURL = request.getHeader("Referer");
        }
        request.setAttribute("redirectURL", redirectURL);

        return "login/loginForm";
    }

    @PostMapping("/login")
    public String loginFilter(
            @Valid @ModelAttribute LoginForm form,
            BindingResult bindingResult,
            HttpServletRequest request,
            @RequestParam(required = false) String redirectURL) {
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

        // 로그인 성공 후 원래 URL로 리다이렉트 (없으면 홈으로 이동)
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
    public String cancelLogin(@RequestParam(required = false) String redirectURL) {
        // 취소 버튼 클릭 시 전달받은 redirectURL로 이동 (없으면 홈으로 이동)
        return "redirect:" + (redirectURL != null && !redirectURL.isBlank() ? redirectURL : "/");
    }
}
