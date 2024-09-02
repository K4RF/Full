package solo.blog.controller.jpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import solo.blog.annotation.Login;
import solo.blog.controller.session.SessionManager;
import solo.blog.entity.database.tx.Member;
import solo.blog.repository.jpa.tx.MemberJpaRepository;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeJpaController {
    private final MemberJpaRepository memberJpaRepository; // 변경된 부분
    private final SessionManager sessionManager;
    @GetMapping("/")
    public String homeArgumentResolver(@Login Member loginMember, Model model) {
        // 세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            return "home";
        }

        // 세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "redirect:/post/jpa/postList";
    }
}