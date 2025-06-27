package hello.board.controller.api.member;

import hello.board.SessionConst;
import hello.board.controller.api.member.dto.MemberProfileDto;
import hello.board.repository.member.query.dto.MemberQueryDto;
import hello.board.service.member.query.MemberQueryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.Optional;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberQueryService memberQueryService;

    @GetMapping("/profile")
    public MemberProfileDto profile(@SessionAttribute(value = SessionConst.MEMBER_ID, required = false) Long memberId,
                                    HttpServletRequest request) {

        //비로그인 회원 접근
        if (memberId == null) {
            return new MemberProfileDto("비로그인 회원", null);
        }

        Optional<MemberQueryDto> findMember = memberQueryService.findMember(memberId);

        if (findMember.isEmpty()) {
            HttpSession session = request.getSession(false);
            session.invalidate();
            return new MemberProfileDto("비로그인 회원", null);
        }

        //로그인 회원 접근
        return new MemberProfileDto("로그인 회원", findMember.get());
    }
}
