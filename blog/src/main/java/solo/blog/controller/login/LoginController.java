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
    public String loginForm(@ModelAttribute("loginForm") LoginForm form,
                            @RequestParam(value = "redirectURL", required = false) String redirectURL,
                            HttpServletRequest request) {
        // redirectURL이 존재하면 세션에 저장
        if (redirectURL != null && !redirectURL.isEmpty()) {
            request.getSession().setAttribute("redirectURL", redirectURL);
        }
        return "login/loginForm";
    }

    @PostMapping("/login")
    public String loginFilter(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member member = loginService.login(form.getLoginId(), form.getPassword());

        if (member == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 시 세션에 로그인 정보 저장
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, member);

        // 리다이렉트 URL 검증과 사용
        String redirectURL = request.getParameter("redirectURL");
        if (redirectURL == null || redirectURL.isEmpty() || !isSafeRedirectUrl(redirectURL)) {
            redirectURL = "/post/jpa/postList"; // 기본 리다이렉트 페이지
        }
        return "redirect:" + redirectURL;
    }

    private boolean isSafeRedirectUrl(String url) {
        // URL이 도메인 내의 경로로만 구성되었는지 확인하는 로직
        return url.startsWith("/post/jpa/postList"); // 예시: 도메인 내의 URL로 시작하는지 검증
    }

    @PostMapping("/logout")
    public String logoutServlet(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}
