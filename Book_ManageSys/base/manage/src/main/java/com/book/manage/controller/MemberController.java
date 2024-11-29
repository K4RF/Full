package com.book.manage.controller;

import com.book.manage.entity.Member;
import com.book.manage.entity.Role;
import com.book.manage.entity.dto.MemberEditDto;
import com.book.manage.repository.member.MemberRepository;
import com.book.manage.service.member.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
    public String addForm(@ModelAttribute("member") Member member,
                          @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        // 이미 로그인된 경우, 비정상적인 접근을 방지하고 홈으로 리디렉션
        if (loginMember != null) {
            return "redirect:/";  // 이미 로그인된 경우 홈으로 이동
        }
        return "members/addMemberForm";
    }
    @PostMapping("/add")
    public String save(@Validated @ModelAttribute Member member, BindingResult bindingResult) {
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
            return "members/addMemberForm";
        }

        // 회원 등록
        try {
            member.setRole(Role.USER);
            memberService.join(member);
        } catch (Exception e) {
            log.error("Error occurred during member registration", e);
            return "members/addMemberForm";
        }

        // 성공 시 홈으로 리다이렉트
        return "redirect:/";
    }
    // 관리자 회원가입
    @GetMapping("/add/admin")
    public String addAdmin(@ModelAttribute("member") Member member,
                          @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        // 이미 로그인된 경우, 비정상적인 접근을 방지하고 홈으로 리디렉션
        if (loginMember != null) {
            return "redirect:/";  // 이미 로그인된 경우 홈으로 이동
        }
        return "members/addAdminForm";
    }

    // 관리자 저장
    @PostMapping("/add/admin")
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
    @GetMapping("/edit")
    public String showEditForm(@SessionAttribute(value = "loginMember", required = false) Member loginMember,
                               @RequestParam(value = "redirectURL", required = false) String redirectURL,
                               HttpServletRequest request, Model model) {
        if (loginMember == null) {
            redirectURL = (redirectURL != null) ? redirectURL : request.getRequestURI();
            return "redirect:/login?redirectURL=" + redirectURL;
        }
        Long memberId = loginMember.getMemberId();
        MemberEditDto editDto = new MemberEditDto(memberId, loginMember.getNickname(), loginMember.getPassword());
        model.addAttribute("member", editDto);
        return "/members/editMemberForm";
    }

    @PostMapping("/edit")
    public String editMember(@ModelAttribute("member") @Validated MemberEditDto editDto, BindingResult bindingResult,
                             @SessionAttribute(value = "loginMember", required = false) Member loginMember,
                             @RequestParam(value = "redirectURL", required = false) String redirectURL,
                             HttpServletRequest request) {
        if (loginMember == null) {
            redirectURL = (redirectURL != null) ? redirectURL : request.getRequestURI();
            return "redirect:/login?redirectURL=" + redirectURL;
        }

        if (bindingResult.hasErrors()) {
            return "/members/editMemberForm";
        }

        memberService.editMember(editDto);

        if (StringUtils.hasText(editDto.getNickname())) {
            loginMember.setNickname(editDto.getNickname());
            request.getSession().setAttribute("loginMember", loginMember);
        }

        if (StringUtils.hasText(redirectURL)) {
            return "redirect:" + redirectURL;
        }
        return "redirect:/";
    }

    @GetMapping("/edit/cancel")
    public String cancelEdit(@RequestParam(value = "redirectURL", required = false) String redirectURL,
                             HttpServletRequest request) {
        // 로그: 요청 파라미터로 넘어온 redirectURL 값 확인
        log.info("Received redirectURL: {}", redirectURL);

        // 세션에서 redirectURL을 가져오기
        HttpSession session = request.getSession(false);
        if (session != null) {
            String sessionRedirectURL = (String) session.getAttribute("redirectURL");
            log.info("Session contains redirectURL: {}", sessionRedirectURL);

            // redirectURL 값이 세션에 있으면 제거
            if (StringUtils.hasText(sessionRedirectURL)) {
                session.removeAttribute("redirectURL");
                log.info("Removed redirectURL from session");
            }
        } else {
            log.info("Session is null, no redirectURL to remove");
        }

        // 최종적으로 리다이렉트할 URL 로그 출력
        String finalRedirectURL = StringUtils.hasText(redirectURL) ? redirectURL : "/";
        log.info("Redirecting to: {}", finalRedirectURL);

        return "redirect:" + finalRedirectURL;
    }

    @GetMapping("/delete/{memberId}")
    public String deleteMemberShield(@PathVariable Long memberId,
                                     @SessionAttribute(value = "loginMember", required = false) Member loginMember,
                                     HttpServletRequest request) {
        if (loginMember == null) {
            String redirectUrl = request.getRequestURI();
            return "redirect:/login?redirectURL=" + redirectUrl;
        }

        log.warn("잘못된 접근: GET 요청은 허용되지 않습니다. POST 요청을 사용하세요.");
        return "redirect:/login";
    }

    @PostMapping("/delete/{memberId}")
    public String deleteMember(@PathVariable Long memberId, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Member loginMember = (Member) request.getSession().getAttribute("loginMember");

        if (loginMember == null || !loginMember.getMemberId().equals(memberId)) {
            redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
            return "redirect:/login";
        }

        try {
            // 회원 삭제 시 도서와 대출 정보도 함께 삭제
            memberService.deleteMember(memberId);
            request.getSession().invalidate(); // 세션 무효화
            redirectAttributes.addFlashAttribute("message", "회원 탈퇴가 성공적으로 완료되었습니다.");
            return "redirect:/";
        } catch (Exception e) {
            log.error("Error deleting member", e);
            redirectAttributes.addFlashAttribute("error", "회원 탈퇴 중 오류가 발생했습니다.");
            return "redirect:/members/edit";  // 에러 발생 시 수정 페이지로 리디렉션
        }
    }

}
