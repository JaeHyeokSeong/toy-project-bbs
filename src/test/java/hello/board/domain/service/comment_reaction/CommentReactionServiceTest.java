package hello.board.domain.service.comment_reaction;

import hello.board.domain.repository.comment_reaction.CommentReactionRepository;
import hello.board.domain.service.board.BoardService;
import hello.board.domain.service.comment.CommentService;
import hello.board.domain.service.member.MemberService;
import hello.board.entity.ReactionType;
import hello.board.entity.board.Board;
import hello.board.entity.comment.Comment;
import hello.board.entity.member.Member;
import hello.board.exception.CommentNotFoundException;
import hello.board.exception.MemberNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class CommentReactionServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    BoardService boardService;
    @Autowired
    CommentService commentService;
    @Autowired
    CommentReactionService commentReactionService;
    @Autowired
    private CommentReactionRepository commentReactionRepository;

    @Test
    void 답변_좋아요1개() {
        //given
        //회원 가입
        Member member = new Member("member1", "email1", "password1");
        memberService.join(member);

        //게시물 작성
        Board board = boardService.saveBoard(member.getId(), "게시물1", "내용1", new ArrayList<>());

        //답변 작성
        Comment comment = commentService.addComment(board.getId(), member.getId(), "답변1", null);

        //when
        commentReactionService.reflectReaction(comment.getId(), member.getId(), ReactionType.LIKE);

        //then
        long result = commentReactionService.totalReactionCount(comment.getId());
        assertThat(result).isEqualTo(1);
    }

    @Test
    void 답변_싫어요1개() {
        //given
        //회원 가입
        Member member = new Member("member1", "email1", "password1");
        memberService.join(member);

        //게시물 작성
        Board board = boardService.saveBoard(member.getId(), "게시물1", "내용1", new ArrayList<>());

        //답변 작성
        Comment comment = commentService.addComment(board.getId(), member.getId(), "답변1", null);

        //when
        commentReactionService.reflectReaction(comment.getId(), member.getId(), ReactionType.DISLIKE);

        //then
        long result = commentReactionService.totalReactionCount(comment.getId());
        assertThat(result).isEqualTo(-1);
    }

    @Test
    void 답변_좋아요2_싫어요1개() {
        //given
        //회원 가입
        Member member1 = new Member("member1", "email1", "password1");
        memberService.join(member1);
        Member member2 = new Member("member2", "email2", "password2");
        memberService.join(member2);
        Member member3 = new Member("member3", "email3", "password3");
        memberService.join(member3);

        //게시물 작성
        Board board = boardService.saveBoard(member1.getId(), "게시물1", "내용1", new ArrayList<>());

        //답변 작성
        Comment comment = commentService.addComment(board.getId(), member1.getId(), "답변1", null);

        //when
        commentReactionService.reflectReaction(comment.getId(), member1.getId(), ReactionType.LIKE);
        commentReactionService.reflectReaction(comment.getId(), member2.getId(), ReactionType.LIKE);
        commentReactionService.reflectReaction(comment.getId(), member3.getId(), ReactionType.DISLIKE);

        //then
        long result = commentReactionService.totalReactionCount(comment.getId());
        assertThat(result).isEqualTo(1);
    }

    @Test
    void 답변_좋아요후_다시_좋아요_취소목적() {
        //given
        //회원 가입
        Member member1 = new Member("member1", "email1", "password1");
        memberService.join(member1);

        //게시물 작성
        Board board = boardService.saveBoard(member1.getId(), "게시물1", "내용1", new ArrayList<>());

        //답변 작성
        Comment comment = commentService.addComment(board.getId(), member1.getId(), "답변1", null);

        //when
        commentReactionService.reflectReaction(comment.getId(), member1.getId(), ReactionType.LIKE);
        commentReactionService.reflectReaction(comment.getId(), member1.getId(), ReactionType.LIKE);

        //then
        long result = commentReactionService.totalReactionCount(comment.getId());
        assertThat(result).isEqualTo(0);
    }

    @Test
    void 답변_싫어요후_다시_싫어요_취소목적() {
        //given
        //회원 가입
        Member member1 = new Member("member1", "email1", "password1");
        memberService.join(member1);

        //게시물 작성
        Board board = boardService.saveBoard(member1.getId(), "게시물1", "내용1", new ArrayList<>());

        //답변 작성
        Comment comment = commentService.addComment(board.getId(), member1.getId(), "답변1", null);

        //when
        commentReactionService.reflectReaction(comment.getId(), member1.getId(), ReactionType.DISLIKE);
        commentReactionService.reflectReaction(comment.getId(), member1.getId(), ReactionType.DISLIKE);

        //then
        long result = commentReactionService.totalReactionCount(comment.getId());
        assertThat(result).isEqualTo(0);
    }

    @Test
    void 답변_좋아요후_다시_싫어요_변경목적() {
        //given
        //회원 가입
        Member member1 = new Member("member1", "email1", "password1");
        memberService.join(member1);

        //게시물 작성
        Board board = boardService.saveBoard(member1.getId(), "게시물1", "내용1", new ArrayList<>());

        //답변 작성
        Comment comment = commentService.addComment(board.getId(), member1.getId(), "답변1", null);

        //when
        commentReactionService.reflectReaction(comment.getId(), member1.getId(), ReactionType.LIKE);
        commentReactionService.reflectReaction(comment.getId(), member1.getId(), ReactionType.DISLIKE);

        //then
        long result = commentReactionService.totalReactionCount(comment.getId());
        assertThat(result).isEqualTo(-1);
    }

    @Test
    void 답변_싫어요후_다시_좋아요_변경목적() {
        //given
        //회원 가입
        Member member1 = new Member("member1", "email1", "password1");
        memberService.join(member1);

        //게시물 작성
        Board board = boardService.saveBoard(member1.getId(), "게시물1", "내용1", new ArrayList<>());

        //답변 작성
        Comment comment = commentService.addComment(board.getId(), member1.getId(), "답변1", null);

        //when
        commentReactionService.reflectReaction(comment.getId(), member1.getId(), ReactionType.DISLIKE);
        commentReactionService.reflectReaction(comment.getId(), member1.getId(), ReactionType.LIKE);

        //then
        long result = commentReactionService.totalReactionCount(comment.getId());
        assertThat(result).isEqualTo(1);
    }

    @Test
    void 답글1개_좋아요1개() {
        //given
        //회원 가입
        Member member1 = new Member("member1", "email1", "password1");
        memberService.join(member1);

        //게시물 작성
        Board board = boardService.saveBoard(member1.getId(), "게시물1", "내용1", new ArrayList<>());
        Comment parentComment = commentService.addComment(board.getId(), member1.getId(), "답변1", null);

        //답글 작성
        Comment childComment = commentService.addComment(board.getId(), member1.getId(), "답변1의 답글1", parentComment.getId());

        //when
        commentReactionService.reflectReaction(childComment.getId(), member1.getId(), ReactionType.LIKE);

        //then
        int totalCommentReaction = commentReactionRepository.findAll().size();
        assertThat(totalCommentReaction).isEqualTo(1);

        long result = commentReactionService.totalReactionCount(childComment.getId());
        assertThat(result).isEqualTo(1);
    }

    @Test
    void 답글1개_싫어요1개() {
        //given
        //회원 가입
        Member member1 = new Member("member1", "email1", "password1");
        memberService.join(member1);

        //게시물 작성
        Board board = boardService.saveBoard(member1.getId(), "게시물1", "내용1", new ArrayList<>());
        Comment parentComment = commentService.addComment(board.getId(), member1.getId(), "답변1", null);

        //답글 작성
        Comment childComment = commentService.addComment(board.getId(), member1.getId(), "답변1의 답글1", parentComment.getId());

        //when
        commentReactionService.reflectReaction(childComment.getId(), member1.getId(), ReactionType.DISLIKE);

        //then
        int totalCommentReaction = commentReactionRepository.findAll().size();
        assertThat(totalCommentReaction).isEqualTo(1);

        long result = commentReactionService.totalReactionCount(childComment.getId());
        assertThat(result).isEqualTo(-1);
    }

    @Test
    void 답글1개_좋아요1개_싫어요2개() {
        //given
        //회원 가입
        Member member1 = new Member("member1", "email1", "password1");
        memberService.join(member1);
        Member member2 = new Member("member2", "email2", "password2");
        memberService.join(member2);
        Member member3 = new Member("member3", "email3", "password3");
        memberService.join(member3);

        //게시물 작성
        Board board = boardService.saveBoard(member1.getId(), "게시물1", "내용1", new ArrayList<>());
        Comment parentComment = commentService.addComment(board.getId(), member1.getId(), "답변1", null);

        //답글 작성
        Comment childComment = commentService.addComment(board.getId(), member1.getId(), "답변1의 답글1", parentComment.getId());

        //when
        commentReactionService.reflectReaction(childComment.getId(), member1.getId(), ReactionType.LIKE);
        commentReactionService.reflectReaction(childComment.getId(), member2.getId(), ReactionType.DISLIKE);
        commentReactionService.reflectReaction(childComment.getId(), member3.getId(), ReactionType.DISLIKE);

        //then
        int totalCommentReaction = commentReactionRepository.findAll().size();
        assertThat(totalCommentReaction).isEqualTo(3);

        long result = commentReactionService.totalReactionCount(childComment.getId());
        assertThat(result).isEqualTo(-1);
    }

    @Test
    void 답글1개_싫어요1개_다시_싫어요_변경목적() {
        //given
        //회원 가입
        Member member1 = new Member("member1", "email1", "password1");
        memberService.join(member1);

        //게시물 작성
        Board board = boardService.saveBoard(member1.getId(), "게시물1", "내용1", new ArrayList<>());
        Comment parentComment = commentService.addComment(board.getId(), member1.getId(), "답변1", null);

        //답글 작성
        Comment childComment = commentService.addComment(board.getId(), member1.getId(), "답변1의 답글1", parentComment.getId());
        commentReactionService.reflectReaction(childComment.getId(), member1.getId(), ReactionType.DISLIKE);

        //when
        commentReactionService.reflectReaction(childComment.getId(), member1.getId(), ReactionType.DISLIKE);

        //then
        int totalCommentReaction = commentReactionRepository.findAll().size();
        assertThat(totalCommentReaction).isEqualTo(0);

        long result = commentReactionService.totalReactionCount(childComment.getId());
        assertThat(result).isEqualTo(0);
    }

    @Test
    void 답글1개_싫어요1개_다시_좋아요1개로변경_변경목적() {
        //given
        //회원 가입
        Member member1 = new Member("member1", "email1", "password1");
        memberService.join(member1);

        //게시물 작성
        Board board = boardService.saveBoard(member1.getId(), "게시물1", "내용1", new ArrayList<>());
        Comment parentComment = commentService.addComment(board.getId(), member1.getId(), "답변1", null);

        //답글 작성
        Comment childComment = commentService.addComment(board.getId(), member1.getId(), "답변1의 답글1", parentComment.getId());
        commentReactionService.reflectReaction(childComment.getId(), member1.getId(), ReactionType.DISLIKE);

        //when
        commentReactionService.reflectReaction(childComment.getId(), member1.getId(), ReactionType.LIKE);

        //then
        int totalCommentReaction = commentReactionRepository.findAll().size();
        assertThat(totalCommentReaction).isEqualTo(1);

        long result = commentReactionService.totalReactionCount(childComment.getId());
        assertThat(result).isEqualTo(1);
    }

    @Test
    void 답글반응실패_존재하지않는_commentId() {
        //given
        //회원 가입
        Member member1 = new Member("member1", "email1", "password1");
        memberService.join(member1);

        //게시물 작성
        Board board = boardService.saveBoard(member1.getId(), "게시물1", "내용1", new ArrayList<>());
        Comment parentComment = commentService.addComment(board.getId(), member1.getId(), "답변1", null);

        //답글 작성
        Comment childComment = commentService.addComment(board.getId(), member1.getId(), "답변1의 답글1", parentComment.getId());
        commentReactionService.reflectReaction(childComment.getId(), member1.getId(), ReactionType.DISLIKE);

        //when then
        assertThatThrownBy(() -> commentReactionService.reflectReaction(childComment.getId() + 1, member1.getId(), ReactionType.LIKE))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void 답글반응실패_존재하지않는_memberId() {
        //given
        //회원 가입
        Member member1 = new Member("member1", "email1", "password1");
        memberService.join(member1);

        //게시물 작성
        Board board = boardService.saveBoard(member1.getId(), "게시물1", "내용1", new ArrayList<>());
        Comment parentComment = commentService.addComment(board.getId(), member1.getId(), "답변1", null);

        //답글 작성
        Comment childComment = commentService.addComment(board.getId(), member1.getId(), "답변1의 답글1", parentComment.getId());
        commentReactionService.reflectReaction(childComment.getId(), member1.getId(), ReactionType.DISLIKE);

        //when then
        assertThatThrownBy(() -> commentReactionService.reflectReaction(childComment.getId(), member1.getId() + 1, ReactionType.LIKE))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 답글반응조회실패_존재하지않는_commentId() {
        //given
        //회원 가입
        Member member1 = new Member("member1", "email1", "password1");
        memberService.join(member1);

        //게시물 작성
        Board board = boardService.saveBoard(member1.getId(), "게시물1", "내용1", new ArrayList<>());
        Comment parentComment = commentService.addComment(board.getId(), member1.getId(), "답변1", null);

        //답글 작성
        Comment childComment = commentService.addComment(board.getId(), member1.getId(), "답변1의 답글1", parentComment.getId());
        commentReactionService.reflectReaction(childComment.getId(), member1.getId(), ReactionType.DISLIKE);

        //when
        commentReactionService.reflectReaction(childComment.getId(), member1.getId(), ReactionType.LIKE);

        //then
        assertThatThrownBy(() -> commentReactionService.totalReactionCount(childComment.getId() + 1))
                .isInstanceOf(CommentNotFoundException.class);
    }

}