package solo.blog.controller.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import solo.blog.annotation.Login;
import solo.blog.controller.session.SessionConst;
import solo.blog.entity.database.tx.Member;

@Slf4j
@Component
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
        boolean isMemberType = Member.class.isAssignableFrom(parameter.getParameterType());
        return hasLoginAnnotation && isMemberType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        HttpSession session = request.getSession(false);

        log.info("Resolving argument for @Login parameter");

        if (session == null) {
            log.info("No active session found.");
            return null;
        }

        Object loginMember = session.getAttribute(SessionConst.LOGIN_MEMBER);
        log.info("Retrieved member from session: {}", loginMember);

        if (loginMember instanceof Member) {
            Member member = (Member) loginMember;
            log.info("Member details - ID: {}, LoginId: {}, Name: {}", member.getId(), member.getLoginId(), member.getName());
        } else {
            log.error("Retrieved object is not of type Member or is null.");
        }

        return loginMember;
    }
}
