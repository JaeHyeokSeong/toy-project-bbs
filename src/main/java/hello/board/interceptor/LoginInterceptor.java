package hello.board.interceptor;

import hello.board.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        //로그인한 사용자 접근
        if (session != null && session.getAttribute(SessionConst.MEMBER_ID) != null) {
            return true;
        }

        //비로그인한 사용자 접근
        String encodedURI = getEncodedURI(request);
        response.sendRedirect("/login?redirectURL=" + encodedURI);
        return false;
    }

    private static String getEncodedURI(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();
        log.info("requestURI={}", requestURI);
        log.info("queryString={}", queryString);
        if (queryString != null) {
            requestURI = requestURI + "?" + queryString;
        }
        log.info("최종 requestURI={}", requestURI);
        return UriUtils.encode(requestURI, StandardCharsets.UTF_8);
    }
}
