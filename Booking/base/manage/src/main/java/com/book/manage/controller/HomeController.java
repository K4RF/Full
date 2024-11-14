package com.book.manage.controller;

import com.book.manage.annotation.Login;
import com.book.manage.controller.login.session.SessionConst;
import com.book.manage.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    //@GetMapping("/")
    public String homeLogin(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, Model model) {
        // 세션에 회원 데이터가 없으면 false
        if (loginMember == null) {
            return "home";
        }

        // 세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    @GetMapping("/")
    public String home(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, Model model) {
        // 로그인 여부에 따라 다른 화면을 보여줍니다.
        if (loginMember != null) {
            // 로그인된 경우, 회원 정보를 넘겨줍니다.
            model.addAttribute("loginMember", loginMember);
        }
        return "home"; // home.html을 그대로 반환
    }
}
