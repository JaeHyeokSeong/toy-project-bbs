package hello.board.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.board.SessionConst;
import hello.board.service.member.MemberService;
import hello.board.dto.ResponseResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class ApiLoginInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;
    private final MemberService memberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        //로그인한 사용자 접근
        if (session != null && session.getAttribute(SessionConst.MEMBER_ID) != null) {
            return memberService.findById((Long) session.getAttribute(SessionConst.MEMBER_ID)).isPresent();
        }

        //비로그인 사용자 접근
        ResponseResult responseResult = new ResponseResult(
                HttpStatus.UNAUTHORIZED.toString(),
                "접근 권한이 없습니다.",
                "로그인이 필요합니다.");

        String value = objectMapper.writeValueAsString(responseResult);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(value);
        return false;
    }
}
