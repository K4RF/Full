package com.book.manage.controller.login;

import com.book.manage.controller.login.session.SessionConst;
import com.book.manage.entity.LoginForm;
import com.book.manage.entity.Member;
import com.book.manage.service.LoginService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jdk.jfr.Frequency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form, HttpServletRequest request) {
        // 로그인된 사용자인지 확인
        HttpSession session = request.getSession(false);
        Member loginMember = (Member) (session != null ? session.getAttribute(SessionConst.LOGIN_MEMBER) : null);
        return "login/loginForm";
    }

    @PostMapping("/login")
    public String loginFilter(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletRequest request) {
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
        
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logoutServlet(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}
