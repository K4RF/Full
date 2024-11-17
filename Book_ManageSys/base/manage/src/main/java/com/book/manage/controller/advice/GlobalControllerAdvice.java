package com.book.manage.controller.advice;

import com.book.manage.entity.Member;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("loginMember")
    public Member addLoginMemberToModel(@SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        return loginMember;
    }

    // 현재 요청 URL 정보를 모든 컨트롤러에 전달
    @ModelAttribute("redirectURL")
    public String addRedirectUrlToModel(HttpServletRequest request) {
        return request.getRequestURI();
    }
}