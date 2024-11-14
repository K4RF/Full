package com.book.manage.controller;

import com.book.manage.entity.Member;
import com.book.manage.entity.dto.MemberEditDto;
import com.book.manage.repository.member.MemberRepository;
import com.book.manage.service.member.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/members")
public class MemberController {
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @ModelAttribute
    public void addLoginMemberToModel(@SessionAttribute(value = "loginMember", required = false) Member loginMember, Model model) {
        model.addAttribute("loginMember", loginMember);
    }

    @GetMapping("/add")
    public String addForm(@ModelAttribute("member") Member member) {
        return "members/addMemberForm";
    }

    @PostMapping("/add")
    public String save(@Validated @ModelAttribute Member member, BindingResult bindingResult) {
        // 로그인 ID 유효성 검사: 영어로 16자 이내
        if (!member.getLoginId().matches("^[a-zA-Z0-9]{1,16}$")) {
            bindingResult.rejectValue("loginId", "invalidFormat", "로그인 ID는 영어로 16자 이내여야 합니다.");
        }

        // 비밀번호 유효성 검사: 8~16글자
        if (!member.getPassword().matches("^[a-zA-Z0-9]{8,16}$")) {
            bindingResult.rejectValue("password", "invalidLength", "비밀번호는 8자에서 16자 사이여야 합니다.");
        }

        // 이름 유효성 검사: 특수문자 제외 및 예약된 이름 차단
        if (!member.getNickname().matches("^[a-zA-Z가-힣 ]+$")) {
            bindingResult.rejectValue("nickname", "invalidFormat", "이름에 특수 문자가 포함될 수 없습니다.");
        } else if ("deletedUser".equalsIgnoreCase(member.getNickname())) {
            bindingResult.rejectValue("nickname", "inappropriateName", "부적절한 이름입니다.");
        }

        // 중복 아이디 체크
        if (memberRepository.findByLoginId(member.getLoginId()).isPresent()) {
            bindingResult.rejectValue("loginId", "duplicate", "이미 존재하는 로그인 ID입니다.");
        }

        // 오류가 있을 경우 다시 폼으로 이동
        if (bindingResult.hasErrors()) {
            return "members/addMemberForm";
        }
        try {
            memberService.join(member);
        } catch (Exception e) {
            log.error("Error occurred during member registration", e);
            return "members/addMemberForm";
        }
        return "redirect:/";
    }

    @GetMapping("/edit")
    public String showEditForm(@SessionAttribute(value = "loginMember", required = false) Member loginMember, HttpServletRequest request, Model model) {
        // 로그인되지 않은 경우 로그인 페이지로 리다이렉트 처리
        if (loginMember == null) {
            return "redirect:/login";
        }
        Long memberId = loginMember.getMemberId();
        MemberEditDto editDto = new MemberEditDto(memberId, loginMember.getNickname(), loginMember.getPassword());
        model.addAttribute("member", editDto);
        return "/members/editMemberForm";
    }

    @PostMapping("/edit")
    public String editMember(@ModelAttribute("member") @Validated MemberEditDto editDto, BindingResult bindingResult,
                             @SessionAttribute(value = "loginMember", required = false) Member loginMember, HttpServletRequest request) {
        // 이름이 입력되었을 때만 유효성 검사 실행
        if (StringUtils.hasText(editDto.getNickname())) {
            if (!editDto.getNickname().matches("^[a-zA-Z가-힣 ]+$")) {
                bindingResult.rejectValue("nickname", "invalidFormat", "이름에는 특수문자가 포함될 수 없습니다");
            }
        }

        // 이름 중복 확인
        Optional<Member> existingMember = memberRepository.findByName(editDto.getNickname());
        if (existingMember.isPresent() && !existingMember.get().getMemberId().equals(editDto.getMemberId())) {
            bindingResult.rejectValue("nickname", "duplicate", "이미 존재하는 닉네임입니다");
        }

        // 비밀번호가 입력되었을 때만 유효성 검사 수행
        if (StringUtils.hasText(editDto.getPassword())) {
            if (!editDto.getPassword().matches("^[a-zA-Z0-9]{8,16}$")) {
                bindingResult.rejectValue("password", "invalidLength", "비밀번호는 8자에서 16자 사이여야 합니다.");
            }
        }

        // 오류가 있을 경우 다시 폼으로 이동
        if (bindingResult.hasErrors()) {
            return "/members/editMemberForm";
        }
        //회원 정보 업데이트
        memberService.editMember(editDto);

        // 세션 업데이트
        if (StringUtils.hasText(editDto.getNickname())) {
            loginMember.setNickname(editDto.getNickname());
            request.getSession().setAttribute("loginMember", loginMember);
        }
        return "redirect:/";
    }

    // 회원 탈퇴
    @PostMapping("/delete/{memberId}")
    public String deleteMember(@PathVariable Long memberId, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Member loginMember = (Member) request.getSession().getAttribute("loginMember");

        if (loginMember == null || !loginMember.getMemberId().equals(memberId)) {
            redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
            return "redirect:/login";
        }

        try {
            memberService.deleteMember(memberId);  // 실제 회원 삭제 서비스 호출
            request.getSession().invalidate();  // 세션 무효화
            redirectAttributes.addFlashAttribute("message", "회원 탈퇴가 성공적으로 완료되었습니다.");
            return "redirect:/";  // 홈 화면으로 리디렉션
        } catch (Exception e) {
            log.error("Error deleting member", e);
            redirectAttributes.addFlashAttribute("error", "회원 탈퇴 중 오류가 발생했습니다.");
            return "redirect:/members/edit";  // 에러 발생 시 수정 페이지로 리디렉션
        }
    }
}

