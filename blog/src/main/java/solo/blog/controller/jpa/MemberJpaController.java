package solo.blog.controller.jpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import solo.blog.entity.database.tx.Member;
import solo.blog.repository.jpa.tx.MemberJpaRepository;
import solo.blog.service.jpa.tx.MemberJpaService;

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
    public String save(@Validated @ModelAttribute Member member, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "members/addMemberForm";
        }

        try {
            memberJpaService.join(member);
            if (memberJpaRepository.findByLoginId(member.getLoginId()).isPresent()) {
                bindingResult.rejectValue("loginId", "duplicate", "이미 존재하는 로그인 ID입니다.");
                return "members/addMemberForm";
            }
        } catch (Exception e) {
            // 전체 예외 스택 트레이스를 로그에 출력
            log.error("Error occurred during member registration", e);
            bindingResult.reject("saveFail", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
            return "members/addMemberForm";
        }

        return "redirect:/";
    }
}