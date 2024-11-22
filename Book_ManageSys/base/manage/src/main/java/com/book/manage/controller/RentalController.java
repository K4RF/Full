package com.book.manage.controller;

import com.book.manage.entity.Rental;
import com.book.manage.entity.Member;
import com.book.manage.service.book.RentalService;
import com.book.manage.login.session.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @GetMapping("/rentalList")
    public String rentalList(HttpServletRequest request, Model model) {
        // 세션에서 로그인한 사용자 정보 가져오기
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/login?redirectURL=/rentalList"; // 로그인되지 않았으면 로그인 페이지로 리다이렉트
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if (loginMember == null) {
            return "redirect:/login?redirectURL=/rentalList"; // 세션에 로그인 정보가 없으면 로그인 페이지로 리다이렉트
        }

        // 로그인한 사용자의 대출 목록 조회
        List<Rental> rentals = rentalService.getRentalByMember(loginMember.getMemberId());
        model.addAttribute("rentals", rentals);

        return "book/rentalList"; // 대출 목록 뷰 렌더링
    }
}
