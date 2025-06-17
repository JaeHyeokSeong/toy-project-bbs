package hello.board;

import hello.board.interceptor.LoggedInInterceptor;
import hello.board.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoggedInInterceptor())
                .order(1)
                .addPathPatterns("/signup", "/login");

        registry.addInterceptor(new LoginInterceptor())
                .order(2)
                .addPathPatterns("/account", "/bbs/write", "/bbs/edit/*");
    }
}
