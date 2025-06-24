package hello.board.domain.service.comment.query;

import hello.board.domain.repository.comment.query.dto.CommentDto;
import hello.board.domain.repository.comment.query.dto.CommentSearchDto;
import hello.board.domain.repository.comment.query.dto.CommentSearchSort;
import hello.board.domain.service.board.BoardService;
import hello.board.domain.service.comment.CommentService;
import hello.board.domain.service.member.MemberService;
import hello.board.entity.Board;
import hello.board.entity.Comment;
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
        CommentSearchDto searchDto = new CommentSearchDto(null, CommentSearchSort.OLDEST);

        Slice<CommentDto> result = commentQueryService
                .findAllComments(board.getId(), null, searchDto, PageRequest.of(0, 5));

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
        CommentSearchDto searchDto = new CommentSearchDto(null, CommentSearchSort.OLDEST);

        Slice<CommentDto> result = commentQueryService
                .findAllComments(board.getId(), null, searchDto, PageRequest.of(1, 5));

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
        CommentSearchDto searchDto = new CommentSearchDto(null, CommentSearchSort.OLDEST);

        Slice<CommentDto> result = commentQueryService
                .findAllComments(board.getId(), null, searchDto, PageRequest.of(1, 15));

        //then
        assertThat(result.getContent().size()).isEqualTo(5);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.getContent())
                .extracting("content")
                .containsExactly("comment15", "comment16", "comment17", "comment18", "comment19");
    }


    @Test
    @DisplayName("1번째 부모댓글의 자식댓글 조회하기, page=0, size=5")
    void 부모댓글의_자식댓글_모두조회1() {
        //given
        Member member1 = new Member("name1", "email1", "password1");
        Member member2 = new Member("name2", "email2", "password2");
        memberService.join(member1);
        memberService.join(member2);

        Board board1 = boardService.saveBoard(member1.getId(), "title1", "content1", new ArrayList<>());
        Board board2 = boardService.saveBoard(member2.getId(), "title2", "content2", new ArrayList<>());

        //board1에서 부모 댓글 작성 - 깊이0
        Comment comment1 = commentService.addComment(board1.getId(), member1.getId(), "comment1", null);
        //board2에서 부모 댓글 작성 - 깊이0
        Comment comment2 = commentService.addComment(board2.getId(), member2.getId(), "comment2", null);

        //board1에서 comment1의 자식 댓글 작성 - 깊이1
        for (int i = 0; i < 10; i++) {
            Long memberId = i % 2 == 0 ? member2.getId() : member1.getId();
            commentService.addComment(board1.getId(), memberId, "board1,comment1,content" + i, comment1.getId());
        }

        //board2에서 comment2의 자식 댓글 작성 - 깊이1
        for (int i = 0; i < 10; i++) {
            Long memberId = i % 2 == 0 ? member2.getId() : member1.getId();
            commentService.addComment(board2.getId(), memberId, "board2,comment2,content" + i, comment2.getId());
        }

        //when
        CommentSearchDto searchDto = new CommentSearchDto(comment1.getId(), CommentSearchSort.OLDEST);

        Slice<CommentDto> result = commentQueryService.findAllComments(board1.getId(), null,
                searchDto, PageRequest.of(0, 3));

        //then
        assertThat(result.getNumberOfElements()).isEqualTo(3);
        assertThat(result.getContent()).extracting("content")
                .containsExactly("board1,comment1,content0", "board1,comment1,content1", "board1,comment1,content2");
    }
}