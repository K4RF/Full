package solo.blog.controller.jpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import solo.blog.entity.database.tx.Member;
import solo.blog.model.MemberUpdateDto;
import solo.blog.repository.jpa.tx.MemberJpaRepository;
import solo.blog.service.jpa.tx.MemberJpaService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/members")
public class MemberJpaController {
    private final MemberJpaRepository memberJpaRepository;
    private final MemberJpaService memberJpaService;

    // 모든 요청에서 loginMember 모델을 추가
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
        if (!member.getName().matches("^[a-zA-Z가-힣 ]+$")) {
            bindingResult.rejectValue("name", "invalidFormat", "이름에 특수 문자가 포함될 수 없습니다.");
        } else if ("deletedUser".equalsIgnoreCase(member.getName())) {
            bindingResult.rejectValue("name", "inappropriateName", "부적절한 이름입니다.");
        }

        // 중복 아이디 체크
        if (memberJpaRepository.findByLoginId(member.getLoginId()).isPresent()) {
            bindingResult.rejectValue("loginId", "duplicate", "이미 존재하는 로그인 ID입니다.");
        }

        // 오류가 있을 경우 다시 폼으로 이동
        if (bindingResult.hasErrors()) {
            return "members/addMemberForm";
        }

        try {
            memberJpaService.join(member);
        } catch (Exception e) {
            log.error("Error occurred during member registration", e);
            bindingResult.reject("saveFail", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
            return "members/addMemberForm";
        }

        return "redirect:/";
    }

    @GetMapping("/update")
    public String showUpdateForm(@SessionAttribute(value = "loginMember", required = false) Member loginMember, HttpServletRequest request, Model model) {
        // 로그인되지 않은 경우 로그인 페이지로 리다이렉트하며, 원래 URL을 함께 전달
        if (loginMember == null) {
            String redirectURL = request.getRequestURI();
            return "redirect:/login?redirectURL=" + redirectURL;
        }

        Long memberId = loginMember.getId(); // loginMember에서 memberId 가져오기
        MemberUpdateDto updateDto = new MemberUpdateDto(memberId, loginMember.getName(), loginMember.getPassword());
        model.addAttribute("member", updateDto);
        return "/members/updateMemberForm";
    }

    @PostMapping("/update")
    public String updateMember(@ModelAttribute("member") @Validated MemberUpdateDto updateDto,
                               BindingResult bindingResult,
                               @SessionAttribute(value = "loginMember", required = false) Member loginMember,
                               HttpServletRequest request) {

        // 이름이 입력되었을 때만 유효성 검사 수행
        if (StringUtils.hasText(updateDto.getName())) {
            if (!updateDto.getName().matches("^[a-zA-Z가-힣 ]+$")) {
                bindingResult.rejectValue("name", "invalidFormat", "이름에는 특수 문자가 포함될 수 없습니다.");
            }

            // 이름 중복 확인
            Optional<Member> existingMember = memberJpaRepository.findByName(updateDto.getName());
            if (existingMember.isPresent() && !existingMember.get().getId().equals(updateDto.getMemberId())) {
                bindingResult.rejectValue("name", "duplicate", "이미 존재하는 이름입니다.");
            }
        }

        // 비밀번호가 입력되었을 때만 유효성 검사 수행
        if (StringUtils.hasText(updateDto.getPassword())) {
            if (!updateDto.getPassword().matches("^[a-zA-Z0-9]{8,16}$")) {
                bindingResult.rejectValue("password", "invalidLength", "비밀번호는 8자에서 16자 사이여야 합니다.");
            }
        }

        // 오류가 있을 경우 다시 폼으로 이동
        if (bindingResult.hasErrors()) {
            return "/members/updateMemberForm";
        }

        // 회원 정보 업데이트
        memberJpaService.updateMember(updateDto);

        // 세션 업데이트
        if (StringUtils.hasText(updateDto.getName())) {
            loginMember.setName(updateDto.getName());
            request.getSession().setAttribute("loginMember", loginMember);
        }

        return "redirect:/post/jpa/postList";
    }

    // 회원 탈퇴
    @PostMapping("/delete/{memberId}")
    public String deleteMember(@PathVariable Long memberId, HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {
        Member loginMember = (Member) request.getSession().getAttribute("loginMember");

        if (loginMember == null || !loginMember.getId().equals(memberId)) {
            redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
            return "redirect:/login";
        }

        try {
            memberJpaService.deleteMember(memberId);
            redirectAttributes.addFlashAttribute("message", "회원 탈퇴가 성공적으로 완료되었습니다.");
            request.getSession().invalidate(); // 세션 무효화
        } catch (Exception e) {
            log.error("Error deleting member", e);
            redirectAttributes.addFlashAttribute("error", "회원 탈퇴 중 오류가 발생했습니다.");
            return "redirect:/members/update";
        }

        return "redirect:/";
    }

}
