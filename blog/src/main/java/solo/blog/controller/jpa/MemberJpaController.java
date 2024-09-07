package solo.blog.controller.jpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import solo.blog.entity.database.tx.Member;
import solo.blog.model.MemberUpdateDto;
import solo.blog.repository.jpa.tx.MemberJpaRepository;
import solo.blog.service.jpa.tx.MemberJpaService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/members")
public class MemberJpaController {
    private final MemberJpaRepository memberJpaRepository;
    private final MemberJpaService memberJpaService;

    @GetMapping("/add")
    public String addForm(@ModelAttribute("member") Member member) {
        return "members/addMemberForm";
    }

    @PostMapping("/add")
    public String save(@Validated @ModelAttribute Member member, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "members/addMemberForm";
        }

        // 중복 아이디 체크
        if (memberJpaRepository.findByLoginId(member.getLoginId()).isPresent()) {
            bindingResult.rejectValue("loginId", "duplicate", "이미 존재하는 로그인 ID입니다.");
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

        // loginMember의 id가 올바르게 전달되는지 확인
        Long memberId = loginMember.getId();  // 여기서 memberId를 가져옵니다.

        // `MemberUpdateDto`에 `memberId`를 설정
        MemberUpdateDto updateDto = new MemberUpdateDto(loginMember.getId(), loginMember.getName(), loginMember.getPassword());
        model.addAttribute("member", updateDto);
        return "/members/updateMemberForm";
    }

    @PostMapping("/update")
    public String updateMember(@ModelAttribute("member") @Validated MemberUpdateDto updateDto, BindingResult bindingResult) {
        // ID가 없을 경우 예외 처리
        if (updateDto.getMemberId() == null) {
            bindingResult.rejectValue("memberId", "nullId", "회원 ID가 없습니다.");
            return "/members/updateMemberForm";
        }

        if (bindingResult.hasErrors()) {
            return "/members/updateMemberForm";
        }

        memberJpaService.updateMember(updateDto);
        return "redirect:/post/jpa/postList";
    }

}
