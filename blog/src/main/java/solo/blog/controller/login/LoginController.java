package solo.blog.controller.login;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import solo.blog.controller.session.SessionConst;
import solo.blog.controller.session.SessionManager;
import solo.blog.entity.database.tx.Member;
import solo.blog.entity.v2.LoginForm;
import solo.blog.service.jpa.LoginJpaService;


@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {
    private final LoginJpaService loginService;
    private final SessionManager sessionManager;

    @GetMapping("login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }

    @PostMapping("/login")
    public String loginFilter(@Valid LoginForm form, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member member = loginService.login(form.getLoginId(), form.getPassword());

        if (member == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, member);

        // 로그 추가
        log.info("Session saved with member: {}", member);
        log.info("Saved in session - ID: {}, LoginId: {}, Name: {}", member.getId(), member.getLoginId(), member.getName());

        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logoutServlet(HttpServletRequest request) {
        // 세션을 삭제
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    private static void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
