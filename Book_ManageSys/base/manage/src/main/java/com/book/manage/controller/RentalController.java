package com.book.manage.controller;

import com.book.manage.entity.Rental;
import com.book.manage.entity.Member;
import com.book.manage.entity.dto.RentalSearchDto;
import com.book.manage.service.book.rental.RentalService;
import com.book.manage.login.session.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @GetMapping("/rentalList")
    public String rentalList(
            HttpServletRequest request,
            @ModelAttribute("rentalSearch") RentalSearchDto searchDto,
            Model model) {

        // 세션에서 로그인 사용자 확인
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/login?redirectURL=/rentalList";
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if (loginMember == null) {
            return "redirect:/login?redirectURL=/rentalList";
        }

        // 로그인한 사용자 ID로 검색 조건 설정
        searchDto.setMemberId(loginMember.getMemberId());

        // 검색 조건에 따른 대출 목록 조회
        List<Rental> rentals = rentalService.findRentals(searchDto);
        model.addAttribute("rentals", rentals);

        return "book/rentalList";
    }
}
