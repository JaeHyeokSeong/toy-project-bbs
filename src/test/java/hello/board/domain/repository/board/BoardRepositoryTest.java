package hello.board.domain.repository.board;

import hello.board.service.board.BoardService;
import hello.board.service.member.MemberService;
import hello.board.entity.board.Board;
import hello.board.entity.member.Member;
import hello.board.repository.board.BoardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class BoardRepositoryTest {

    @Autowired
    MemberService memberService;
    @Autowired
    BoardService boardService;
    @Autowired
    BoardRepository boardRepository;

    @Test
    void 게시물_찾기_성공() {
        //given
        Member member = new Member("member", "email", "password");
        memberService.join(member);

        Board board = boardService.saveBoard(member.getId(), "title", "content", new ArrayList<>());

        //when
        Optional<Board> findBoard = boardRepository.findBoard(board.getId(), member.getId());

        //then
        assertThat(findBoard).isPresent();
    }

    @Test
    void 게시물_찾기_실패_회원아이디가_불일치() {
        //given
        Member member = new Member("member", "email", "password");
        memberService.join(member);

        Board board = boardService.saveBoard(member.getId(), "title", "content", new ArrayList<>());

        //when
        Optional<Board> findBoard = boardRepository.findBoard(board.getId(), member.getId() + 1);

        //then
        assertThat(findBoard).isEmpty();
    }

    @Test
    void 게시물_찾기_실패_게시물아이디가_불일치() {
        //given
        Member member = new Member("member", "email", "password");
        memberService.join(member);

        Board board = boardService.saveBoard(member.getId(), "title", "content", new ArrayList<>());

        //when
        Optional<Board> findBoard = boardRepository.findBoard(board.getId() + 1, member.getId());

        //then
        assertThat(findBoard).isEmpty();
    }

    @Test
    void 게시물_찾기_실패_모두_불일치() {
        //given
        Member member = new Member("member", "email", "password");
        memberService.join(member);

        Board board = boardService.saveBoard(member.getId(), "title", "content", new ArrayList<>());

        //when
        Optional<Board> findBoard = boardRepository.findBoard(board.getId() + 1, member.getId() + 1);

        //then
        assertThat(findBoard).isEmpty();
    }
}