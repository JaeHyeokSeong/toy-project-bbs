package hello.board.web.login;

import hello.board.SessionConst;
import hello.board.domain.service.member.MemberService;
import hello.board.web.login.dto.LoginDto;
import jakarta.servlet.http.HttpServletRequest;
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

import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final MemberService memberService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginDto") LoginDto loginDto) {
        return "login/loginForm";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginDto loginDto,
                        BindingResult bindingResult,
                        @RequestParam(required = false) String redirectURL,
                        HttpServletRequest request) {

        log.info("로그인 전달되어진 값={}", loginDto);

        if (bindingResult.hasErrors()) {
            log.info("로그인 검증실패={}", bindingResult);
            return "login/loginForm";
        }

        Optional<Long> loginResult = memberService
                .login(loginDto.getEmail(), loginDto.getPassword());

        if (loginResult.isEmpty()) {
            log.info("로그인 실패");
            bindingResult.reject("LoginFail");
            return "login/loginForm";
        }

        log.info("로그인 성공");
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.MEMBER_ID, loginResult.get());

        if (redirectURL != null) {
            return "redirect:" + redirectURL;
        }
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}
