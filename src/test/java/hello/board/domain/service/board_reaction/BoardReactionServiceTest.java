package hello.board.domain.service.board_reaction;

import hello.board.domain.service.board.BoardService;
import hello.board.domain.service.member.MemberService;
import hello.board.entity.board.Board;
import hello.board.entity.member.Member;
import hello.board.entity.ReactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class BoardReactionServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    BoardService boardService;
    @Autowired
    BoardReactionService boardReactionService;

    @Test
    void 게시물_좋아요1개_추가() {
        //given
        Member member = new Member("memberA", "emailA", "passwordA");
        memberService.join(member);
        Board board = boardService.saveBoard(member.getId(), "titleA", "contentA", new ArrayList<>());

        //when
        boardReactionService.reflectReaction(board.getId(), member.getId(), ReactionType.LIKE);

        //then
        long count = boardReactionService.totalReactionCount(board.getId());
        assertThat(count).isEqualTo(1);
    }

    @Test
    void 게시물_좋아요누른후_다시좋아요누른경우() {
        //given
        Member member = new Member("memberA", "emailA", "passwordA");
        memberService.join(member);
        Board board = boardService.saveBoard(member.getId(), "titleA", "contentA", new ArrayList<>());
        boardReactionService.reflectReaction(board.getId(), member.getId(), ReactionType.LIKE);

        //when
        boardReactionService.reflectReaction(board.getId(), member.getId(), ReactionType.LIKE);

        //then
        long count = boardReactionService.totalReactionCount(board.getId());
        assertThat(count).isEqualTo(0);
    }

    @Test
    void 게시물_좋아요누른후_싫어요누른경우() {
        //given
        Member member = new Member("memberA", "emailA", "passwordA");
        memberService.join(member);
        Board board = boardService.saveBoard(member.getId(), "titleA", "contentA", new ArrayList<>());
        boardReactionService.reflectReaction(board.getId(), member.getId(), ReactionType.LIKE);

        //when
        boardReactionService.reflectReaction(board.getId(), member.getId(), ReactionType.DISLIKE);

        //then
        long count = boardReactionService.totalReactionCount(board.getId());
        assertThat(count).isEqualTo(-1);
    }

    @Test
    void 게시물_좋아요2_싫어요1() {
        //given
        Member memberA = new Member("memberA", "emailA", "passwordA");
        Member memberB = new Member("memberB", "emailB", "passwordB");
        Member memberC = new Member("memberC", "emailC", "passwordC");
        memberService.join(memberA);
        memberService.join(memberB);
        memberService.join(memberC);

        Board board = boardService.saveBoard(memberA.getId(), "titleA", "contentA", new ArrayList<>());

        //when
        boardReactionService.reflectReaction(board.getId(), memberA.getId(), ReactionType.LIKE);
        boardReactionService.reflectReaction(board.getId(), memberB.getId(), ReactionType.LIKE);
        boardReactionService.reflectReaction(board.getId(), memberC.getId(), ReactionType.DISLIKE);

        //then
        long count = boardReactionService.totalReactionCount(board.getId());
        assertThat(count).isEqualTo(1);
    }

    @Test
    void 게시물_좋아요1_싫어요2() {
        //given
        Member memberA = new Member("memberA", "emailA", "passwordA");
        Member memberB = new Member("memberB", "emailB", "passwordB");
        Member memberC = new Member("memberC", "emailC", "passwordC");
        memberService.join(memberA);
        memberService.join(memberB);
        memberService.join(memberC);

        Board board = boardService.saveBoard(memberA.getId(), "titleA", "contentA", new ArrayList<>());

        //when
        boardReactionService.reflectReaction(board.getId(), memberA.getId(), ReactionType.LIKE);
        boardReactionService.reflectReaction(board.getId(), memberB.getId(), ReactionType.DISLIKE);
        boardReactionService.reflectReaction(board.getId(), memberC.getId(), ReactionType.DISLIKE);

        //then
        long count = boardReactionService.totalReactionCount(board.getId());
        assertThat(count).isEqualTo(-1);
    }

    @Test
    void 게시물_반응_없는게시물아이디() {
        //given
        Member memberA = new Member("memberA", "emailA", "passwordA");
        memberService.join(memberA);

        Board board = boardService.saveBoard(memberA.getId(), "titleA", "contentA", new ArrayList<>());

        //when then
        assertThatThrownBy(() -> boardReactionService
                .reflectReaction(board.getId() + 1, memberA.getId(), ReactionType.LIKE))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 게시물_반응_없는사용자아이디() {
        //given
        Member memberA = new Member("memberA", "emailA", "passwordA");
        memberService.join(memberA);

        Board board = boardService.saveBoard(memberA.getId(), "titleA", "contentA", new ArrayList<>());

        //when then
        assertThatThrownBy(() -> boardReactionService
                .reflectReaction(board.getId(), memberA.getId() + 1, ReactionType.LIKE))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 게시물_반응_둘다없는() {
        //given
        Member memberA = new Member("memberA", "emailA", "passwordA");
        memberService.join(memberA);

        Board board = boardService.saveBoard(memberA.getId(), "titleA", "contentA", new ArrayList<>());

        //when then
        assertThatThrownBy(() -> boardReactionService
                .reflectReaction(board.getId() + 1, memberA.getId() + 1, ReactionType.LIKE))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 게시물총2개_게시물1에좋아요하나증가() {
        //given
        Member memberA = new Member("memberA", "emailA", "passwordA");
        Member memberB = new Member("memberB", "emailB", "passwordB");
        Member memberC = new Member("memberC", "emailC", "passwordC");
        memberService.join(memberA);
        memberService.join(memberB);
        memberService.join(memberC);

        Board board1 = boardService.saveBoard(memberA.getId(), "titleA", "contentA", new ArrayList<>());
        Board board2 = boardService.saveBoard(memberA.getId(), "titleB", "contentB", new ArrayList<>());

        //when
        boardReactionService.reflectReaction(board1.getId(), memberA.getId(), ReactionType.LIKE);

        //then
        long count1 = boardReactionService.totalReactionCount(board1.getId());
        assertThat(count1).isEqualTo(1);
        long count2 = boardReactionService.totalReactionCount(board2.getId());
        assertThat(count2).isEqualTo(0);
    }

    @Test
    void 게시물총2개_게시물1에좋아요하나증가_게시물2에좋아요하나그리고싫어요2개() {
        //given
        Member memberA = new Member("memberA", "emailA", "passwordA");
        Member memberB = new Member("memberB", "emailB", "passwordB");
        Member memberC = new Member("memberC", "emailC", "passwordC");
        memberService.join(memberA);
        memberService.join(memberB);
        memberService.join(memberC);

        Board board1 = boardService.saveBoard(memberA.getId(), "titleA", "contentA", new ArrayList<>());
        Board board2 = boardService.saveBoard(memberA.getId(), "titleB", "contentB", new ArrayList<>());

        //when
        boardReactionService.reflectReaction(board2.getId(), memberA.getId(), ReactionType.LIKE);
        boardReactionService.reflectReaction(board2.getId(), memberB.getId(), ReactionType.DISLIKE);
        boardReactionService.reflectReaction(board1.getId(), memberA.getId(), ReactionType.LIKE);
        boardReactionService.reflectReaction(board2.getId(), memberC.getId(), ReactionType.DISLIKE);

        //then
        long count1 = boardReactionService.totalReactionCount(board1.getId());
        assertThat(count1).isEqualTo(1);
        long count2 = boardReactionService.totalReactionCount(board2.getId());
        assertThat(count2).isEqualTo(-1);
    }
}