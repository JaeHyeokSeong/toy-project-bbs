package hello.board.interceptor;

import hello.board.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoggedInInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //이미 로그인한 사용자는 접근 못하게 막기
        HttpSession session = request.getSession(false);

        //로그인한 사용자 접근
        if (session != null && session.getAttribute(SessionConst.MEMBER_ID) != null) {
            response.sendRedirect("/");
            return false;
        }

        //비로그인 사용자
        return true;
    }
}
