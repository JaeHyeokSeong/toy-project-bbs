package hello.board;

import hello.board.domain.service.board.BoardService;
import hello.board.domain.service.member.MemberService;
import hello.board.entity.member.Member;
import hello.board.entity.file.UploadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class DataInit {

    private final MemberService memberService;
    private final BoardService boardService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        //회원 초기화
        Member memberA = new Member("memberA", "memberA@gmail.com", "passwordA");
        Member memberB = new Member("memberB", "memberB@gmail.com", "passwordB");
        memberService.join(memberA);
        memberService.join(memberB);

        //게시물 초기화
        for (int i = 0; i < 150; i++) {
            String title = "title" + i;
            String content = "content" + i;
            ArrayList<UploadFile> files = new ArrayList<>();
            if (i % 2 == 0) {
                boardService.saveBoard(memberA.getId(), title, content, files);
            } else {
                boardService.saveBoard(memberB.getId(), title, content, files);
            }
        }
    }
}
