package com.book.manage.controller;

import com.book.manage.entity.Member;
import com.book.manage.entity.Role;
import com.book.manage.repository.member.MemberRepository;
import com.book.manage.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    // 관리자 회원가입
    @GetMapping("/add")
    public String addAdmin(@ModelAttribute("member") Member member,
                           @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        // 이미 로그인된 경우, 비정상적인 접근을 방지하고 홈으로 리디렉션
        if (loginMember != null) {
            return "redirect:/";  // 이미 로그인된 경우 홈으로 이동
        }
        return "members/addAdminForm";
    }

    // 관리자 저장
    @PostMapping("/add")
    public String saveAdmin(@Validated @ModelAttribute Member member, BindingResult bindingResult) {
        // 로그인 ID 검증
        if (!member.getLoginId().matches("^[a-zA-Z0-9]{1,16}$")) {
            bindingResult.rejectValue("loginId", "invalidFormat", "로그인 ID는 영어와 숫자로 1~16자 이내여야 합니다.");
        }

        // 비밀번호 검증
        if (!member.getPassword().matches("^[a-zA-Z0-9]{8,16}$")) {
            bindingResult.rejectValue("password", "invalidLength", "비밀번호는 8자에서 16자 사이여야 합니다.");
        }

        // 닉네임 검증
        if (!member.getNickname().matches("^[a-zA-Z가-힣0-9 ]+$")) {
            bindingResult.rejectValue("nickname", "invalidFormat", "닉네임에는 특수 문자를 사용할 수 없습니다.");
        } else if ("deletedUser".equalsIgnoreCase(member.getNickname())) {
            bindingResult.rejectValue("nickname", "inappropriateName", "부적절한 이름입니다.");
        }

        // 중복 로그인 ID 확인
        if (memberRepository.findByLoginId(member.getLoginId()).isPresent()) {
            bindingResult.rejectValue("loginId", "duplicate", "이미 존재하는 로그인 ID입니다.");
        }

        // 검증 오류가 있으면 회원 가입 폼으로 되돌림
        if (bindingResult.hasErrors()) {
            return "members/addAdminForm";
        }


        // 회원 등록
        try {
            member.setRole(Role.ADMIN);

            memberService.join(member);
        } catch (Exception e) {
            log.error("Error occurred during member registration", e);
            return "members/addAdminForm";
        }

        // 성공 시 홈으로 리다이렉트
        return "redirect:/";
    }
    @GetMapping
    public String adminHome() {
        return "redirect:admin/members";
    }

    @GetMapping("/members")
    public String manageMembers(Model model) {
        List<Member> members = memberService.findAllMembers(); // 전체 회원 조회
        model.addAttribute("members", members);
        return "admin/adminHome";
    }

    // 추가: 도서 관리, 대출 관리, 구매 관리 매핑
    @GetMapping("/books")
    public String manageBooks() {
        return "admin/manageBooks";
    }

    @GetMapping("/rentals")
    public String manageRentals() {
        return "admin/manageRentals";
    }

    @GetMapping("/orders")
    public String manageOrders() {
        return "admin/manageOrders";
    }
}
