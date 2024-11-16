package com.book.manage.controller.advice;

import com.book.manage.entity.Member;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("loginMember")
    public Member addLoginMemberToModel(@SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        return loginMember;
    }
}