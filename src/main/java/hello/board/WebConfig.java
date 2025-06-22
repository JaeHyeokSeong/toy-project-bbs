package hello.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.board.domain.service.member.MemberService;
import hello.board.interceptor.ApiLoginInterceptor;
import hello.board.interceptor.LoggedInInterceptor;
import hello.board.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ObjectMapper objectMapper;
    private final MemberService memberService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggedInInterceptor())
                .order(1)
                .addPathPatterns("/signup", "/login");

        registry.addInterceptor(new LoginInterceptor(memberService))
                .order(2)
                .addPathPatterns("/account", "/bbs/write", "/bbs/edit/*");

        registry.addInterceptor(new ApiLoginInterceptor(objectMapper, memberService))
                .order(3)
                .addPathPatterns("/api/board-reaction/*", "/api/comment-parent/*");
    }
}
