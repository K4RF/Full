package solo.blog.controller.v3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import solo.blog.entity.v2.Member;
import solo.blog.repository.v2.MemberRepositoryV2;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberRepositoryV2 memberRepositoryV2;

    @GetMapping("/add")
    public String addForm(@ModelAttribute("member") Member member){
        return "members/addMemberForm";
    }

    @PostMapping("/add")
    public String save(@Validated @ModelAttribute Member member, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "members/addMemberForm";
        }

        memberRepositoryV2.save(member);
        return "redirect:/";
    }
}

