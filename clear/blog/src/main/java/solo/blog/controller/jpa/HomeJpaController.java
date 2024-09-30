package solo.blog.controller.jpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import solo.blog.annotation.Login;
import solo.blog.controller.session.SessionConst;
import solo.blog.controller.session.SessionManager;
import solo.blog.entity.database.tx.Member;
import solo.blog.repository.jpa.tx.MemberJpaRepository;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeJpaController {

    //@GetMapping("/")
    public String homeArgumentResolverV2(@Login Member loginMember, Model model) {
        // 세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            return "home";
        }

        // 세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "redirect:/post/jpa/postList";
    }

   // @GetMapping("/")
    public String redirectUrl() {
        return "redirect:/post/jpa/postList";
    }
}