package hello.board.domain.service.comment.query;

import hello.board.domain.repository.comment.query.dto.CommentParentDto;
import hello.board.domain.service.board.BoardService;
import hello.board.domain.service.comment.CommentService;
import hello.board.domain.service.member.MemberService;
import hello.board.entity.Board;
import hello.board.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CommentQueryServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    BoardService boardService;
    @Autowired
    CommentService commentService;
    @Autowired
    CommentQueryService commentQueryService;

    @Test
    @DisplayName("부모댓글 모두 조회, totalContents=30, pageNumber=0, pageSize=5")
    void 부모댓글_모두조회1() {
        //given
        Board board = null;
        for (int i = 0; i < 30; i++) {
            Member member = new Member("name" + i, "email" + i, "password" + i);
            memberService.join(member);

            if (i == 0) {
                board = boardService.saveBoard(member.getId(), "title", "content", new ArrayList<>());
            }

            commentService.addComment(board.getId(), member.getId(), "comment" + i, null);
        }

        //when
        Slice<CommentParentDto> result = commentQueryService
                .findAllCommentsParent(board.getId(), PageRequest.of(0, 5));

        //then
        assertThat(result.getContent().size()).isEqualTo(5);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.getContent())
                .extracting("content")
                .containsExactly("comment0", "comment1", "comment2", "comment3", "comment4");
    }

    @Test
    @DisplayName("부모댓글 모두 조회, totalContents=30, pageNumber=1, pageSize=5")
    void 부모댓글_모두조회2() {
        //given
        Board board = null;
        for (int i = 0; i < 30; i++) {
            Member member = new Member("name" + i, "email" + i, "password" + i);
            memberService.join(member);

            if (i == 0) {
                board = boardService.saveBoard(member.getId(), "title", "content", new ArrayList<>());
            }

            commentService.addComment(board.getId(), member.getId(), "comment" + i, null);
        }

        //when
        Slice<CommentParentDto> result = commentQueryService
                .findAllCommentsParent(board.getId(), PageRequest.of(1, 5));

        //then
        assertThat(result.getContent().size()).isEqualTo(5);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.getContent())
                .extracting("content")
                .containsExactly("comment5", "comment6", "comment7", "comment8", "comment9");
    }

    @Test
    @DisplayName("부모댓글 모두 조회, totalContents=20, pageNumber=1, pageSize=15")
    void 부모댓글_모두조회3() {
        //given
        Board board = null;
        for (int i = 0; i < 20; i++) {
            Member member = new Member("name" + i, "email" + i, "password" + i);
            memberService.join(member);

            if (i == 0) {
                board = boardService.saveBoard(member.getId(), "title", "content", new ArrayList<>());
            }

            commentService.addComment(board.getId(), member.getId(), "comment" + i, null);
        }

        //when
        Slice<CommentParentDto> result = commentQueryService
                .findAllCommentsParent(board.getId(), PageRequest.of(1, 15));

        //then
        assertThat(result.getContent().size()).isEqualTo(5);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.getContent())
                .extracting("content")
                .containsExactly("comment15", "comment16", "comment17", "comment18", "comment19");
    }
}