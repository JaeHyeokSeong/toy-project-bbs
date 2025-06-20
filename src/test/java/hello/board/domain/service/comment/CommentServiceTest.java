package hello.board.domain.service.comment;

import hello.board.domain.repository.comment.CommentRepository;
import hello.board.domain.service.board.BoardService;
import hello.board.domain.service.member.MemberService;
import hello.board.entity.Board;
import hello.board.entity.Comment;
import hello.board.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    BoardService boardService;
    @Autowired
    CommentService commentService;
    @Autowired
    CommentRepository commentRepository;

    @Test
    void 댓글하나_추가() {
        //given
        Member memberA = new Member("memberA", "emailA", "passwordA");
        memberService.join(memberA);

        Board board = boardService.saveBoard(memberA.getId(), "titleA", "contentA", new ArrayList<>());

        //when
        commentService.addComment(board.getId(), memberA.getId(), "댓글1", null);

        //then
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments.size()).isEqualTo(1);
        assertThat(comments.get(0).getBoard()).isSameAs(board);
        assertThat(comments.get(0).getMember()).isSameAs(memberA);
        assertThat(comments.get(0).getContent()).isEqualTo("댓글1");
        assertThat(comments.get(0).getParentComment()).isNull();
    }

    @Test
    void 댓글하나_대댓글하나() {
        //given
        Member memberA = new Member("memberA", "emailA", "passwordA");
        memberService.join(memberA);

        Board savedBoard = boardService
                .saveBoard(memberA.getId(), "titleA", "contentA", new ArrayList<>());
        Comment parentComment = commentService
                .addComment(savedBoard.getId(), memberA.getId(), "댓글1", null);

        //when
        commentService.addComment(savedBoard.getId(), memberA.getId(), "대댓글1", parentComment.getId());

        //then
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments.size()).isEqualTo(2);
        assertThat(comments.get(0).getBoard()).isSameAs(savedBoard);
        assertThat(comments.get(0).getMember()).isSameAs(memberA);
        assertThat(comments.get(0).getContent()).isEqualTo("댓글1");
        assertThat(comments.get(0).getParentComment()).isNull();
        assertThat(comments.get(0).getChildComments().size()).isEqualTo(1);

        assertThat(comments.get(1).getBoard()).isSameAs(savedBoard);
        assertThat(comments.get(1).getMember()).isSameAs(memberA);
        assertThat(comments.get(1).getContent()).isEqualTo("대댓글1");
        assertThat(comments.get(1).getParentComment()).isSameAs(comments.get(0));
    }
}