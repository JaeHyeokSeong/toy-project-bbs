package hello.board.web.signup;

import hello.board.SessionConst;
import hello.board.domain.service.member.MemberService;
import hello.board.entity.Member;
import hello.board.exception.DuplicateEmailException;
import hello.board.web.signup.dto.SignupDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/signup")
@RequiredArgsConstructor
public class SignupController {

    private final MemberService memberService;

    @GetMapping
    public String signupForm(@ModelAttribute("signup") SignupDto signupDto) {
        return "signup/signupForm";
    }

    @PostMapping
    public String signup(@Valid @ModelAttribute("signup") SignupDto signupDto,
                         BindingResult bindingResult,
                         @RequestParam(required = false) String redirectURL,
                         HttpServletRequest request) {

        log.info("회원가입 전달되어진 값={}", signupDto);

        if (bindingResult.hasErrors()) {
            log.info("회원가입 검증실패={}", bindingResult);
            return "signup/signupForm";
        }

        Member member = new Member(
                signupDto.getName(),
                signupDto.getEmail(),
                signupDto.getPassword()
        );

        try {
            memberService.join(member);
            log.info("회원가입 성공, member id={}", member.getId());
            HttpSession session = request.getSession();
            session.setAttribute(SessionConst.MEMBER_ID, member.getId());
            if (redirectURL != null) {
                return "redirect:" + redirectURL;
            }
            return "redirect:/";
        } catch (DuplicateEmailException | DataIntegrityViolationException e) {
            log.info("회원가입 검증실패={}", e.getMessage());
            bindingResult.rejectValue("email", "DuplicateEmail");
            return "signup/signupForm";
        }
    }
}
