package hello.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.board.service.member.MemberService;
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
                .addPathPatterns("/signup", "/login");

        registry.addInterceptor(new LoginInterceptor(memberService))
                .addPathPatterns("/account", "/bbs/write", "/bbs/edit/*", "/bbs/delete/*");

        registry.addInterceptor(new ApiLoginInterceptor(objectMapper, memberService))
                .addPathPatterns(
                        "/api/board-reaction/*",
                        "/api/comment/*",
                        "/api/comment-reaction/*",
                        "/api/file",
                        "/api/file/*"
                );
    }
}
