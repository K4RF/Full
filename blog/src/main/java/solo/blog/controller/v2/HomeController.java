package solo.blog.controller.v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import solo.blog.controller.session.SessionManager;
import solo.blog.entity.v2.Member;
import solo.blog.repository.v2.MemberRepository;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;

    //@GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/")
    public String homeLogin(
            @CookieValue(name = "loginId", required = false) Long loginId, Model model) {
        if (loginId == null) {
            return "home";
        }
        //로그인
        Member loginMember = memberRepository.findById(loginId);
        if (loginMember == null) {
            return "home";
        }
        model.addAttribute("member", loginMember);
        return "loginHome";
    }
}