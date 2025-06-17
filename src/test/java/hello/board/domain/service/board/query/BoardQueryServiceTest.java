package hello.board.domain.service.board.query;

import hello.board.domain.repository.board.query.dto.BoardSearchCondition;
import hello.board.domain.repository.board.query.dto.SearchSort;
import hello.board.domain.repository.board.query.dto.SearchTarget;
import hello.board.domain.service.board.BoardService;
import hello.board.domain.service.board.query.dto.BoardDto;
import hello.board.domain.service.board.query.dto.BoardListDto;
import hello.board.domain.service.board.query.dto.BoardUpdateDto;
import hello.board.domain.service.member.MemberService;
import hello.board.entity.Board;
import hello.board.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class BoardQueryServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    BoardService boardService;
    @Autowired
    BoardQueryService boardQueryService;

    @Test
    void boardDto_찾기() {
        //given
        String name = "member";
        Member member = new Member(name, "email", "password");
        memberService.join(member);

        String title = "title";
        String content = "content";
        Board board = boardService.saveBoard(member.getId(), title, content, new ArrayList<>());

        //when
        BoardDto boardDto = boardQueryService.findBoardDto(board.getId(), board.getSlug(), 1);

        //then
        assertThat(boardDto.getId()).isEqualTo(board.getId());
        assertThat(boardDto.getTitle()).isEqualTo(title);
        assertThat(boardDto.getContent()).isEqualTo(content);
        assertThat(boardDto.getCreatedDate()).isNotNull();
        assertThat(boardDto.getLastModifiedDate()).isNotNull();
        assertThat(boardDto.getUploadedFiles().size()).isEqualTo(0);
        assertThat(boardDto.getName()).isEqualTo(name);
    }

    @Test
    void boardDto_찾기실패() {
        //given
        Member member = new Member("member", "email", "password");
        memberService.join(member);

        String title = "title";
        String content = "content";
        Board board = boardService.saveBoard(member.getId(), title, content, new ArrayList<>());

        //when then
        assertThatThrownBy(() -> boardQueryService.findBoardDto(board.getId() + 1, board.getSlug(), 1))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("게시물X, searchTarget=null&searchTerm=")
    void boardListDto1() {
        //given
        PageRequest pageable = PageRequest.of(0, 3);
        BoardSearchCondition condition = new BoardSearchCondition();
        condition.setSearchTarget(null);

        //when
        Page<BoardListDto> result = boardQueryService.findBoardListDto(condition, pageable);

        //then
        assertThat(result.getContent().size()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("게시물X, searchTarget=null&searchTerm=ㄱ")
    void boardListDto2() {
        //given
        PageRequest pageable = PageRequest.of(0, 3);
        BoardSearchCondition condition = new BoardSearchCondition();
        condition.setSearchTarget(null);
        condition.setSearchTerm("ㄱ");

        //when
        Page<BoardListDto> result = boardQueryService.findBoardListDto(condition, pageable);

        //then
        assertThat(result.getContent().size()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("게시물 10개, searchTarget=null&searchTerm=")
    void boardListDto3() {
        //given
        Member member = new Member("member", "email", "password");
        memberService.join(member);

        for (int i = 0; i < 10; i++) {
            boardService.saveBoard(member.getId(), "title" + i, "content" + i, new ArrayList<>());
        }

        PageRequest pageable = PageRequest.of(0, 3);
        BoardSearchCondition condition = new BoardSearchCondition();
        condition.setSearchTarget(null);

        //when
        Page<BoardListDto> result = boardQueryService.findBoardListDto(condition, pageable);

        //then
        assertThat(result.getContent().size()).isEqualTo(3);
        assertThat(result.getTotalElements()).isEqualTo(10);
        assertThat(result.get()).extracting("title")
                .containsExactly("title9", "title8", "title7");
    }

    @Test
    @DisplayName("게시물 10개, searchTarget=제목&searchTerm=")
    void boardListDto4() {
        //given
        Member member = new Member("member", "email", "password");
        memberService.join(member);

        for (int i = 0; i < 10; i++) {
            boardService.saveBoard(member.getId(), "title" + i, "content" + i, new ArrayList<>());
        }

        PageRequest pageable = PageRequest.of(0, 3);
        BoardSearchCondition condition = new BoardSearchCondition();
        condition.setSearchTarget(SearchTarget.TITLE);

        //when
        Page<BoardListDto> result = boardQueryService.findBoardListDto(condition, pageable);

        //then
        assertThat(result.getContent().size()).isEqualTo(3);
        assertThat(result.getTotalElements()).isEqualTo(10);
    }

    @Test
    @DisplayName("게시물 10개, searchTarget=제목&searchTerm=  ")
    void boardListDto5() {
        //given
        Member member = new Member("member", "email", "password");
        memberService.join(member);

        for (int i = 0; i < 10; i++) {
            boardService.saveBoard(member.getId(), "title" + i, "content" + i, new ArrayList<>());
        }

        PageRequest pageable = PageRequest.of(0, 5);
        BoardSearchCondition condition = new BoardSearchCondition();
        condition.setSearchTarget(SearchTarget.TITLE);
        condition.setSearchTerm("  ");

        //when
        Page<BoardListDto> result = boardQueryService.findBoardListDto(condition, pageable);

        //then
        assertThat(result.getContent().size()).isEqualTo(5);
        assertThat(result.getTotalElements()).isEqualTo(10);
    }

    @Test
    @DisplayName("게시물 10개, searchTarget=제목&searchTerm=title5")
    void boardListDto6() {
        //given
        Member member = new Member("member", "email", "password");
        memberService.join(member);

        for (int i = 0; i < 10; i++) {
            boardService.saveBoard(member.getId(), "title" + i, "content" + i, new ArrayList<>());
        }

        PageRequest pageable = PageRequest.of(0, 5);
        BoardSearchCondition condition = new BoardSearchCondition();
        condition.setSearchTarget(SearchTarget.TITLE);
        String searchTerm = "title5";
        condition.setSearchTerm(searchTerm);

        //when
        Page<BoardListDto> result = boardQueryService.findBoardListDto(condition, pageable);

        //then
        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).extracting("title").containsExactly(searchTerm);
    }

    @Test
    @DisplayName("게시물 10개, searchTarget=내용&searchTerm=title")
    void boardListDto7() {
        //given
        Member member = new Member("member", "email", "password");
        memberService.join(member);

        for (int i = 0; i < 10; i++) {
            boardService.saveBoard(member.getId(), "title" + i, "content" + i, new ArrayList<>());
        }

        PageRequest pageable = PageRequest.of(0, 5);
        BoardSearchCondition condition = new BoardSearchCondition();
        condition.setSearchTarget(SearchTarget.CONTENT);
        String searchTerm = "title";
        condition.setSearchTerm(searchTerm);

        //when
        Page<BoardListDto> result = boardQueryService.findBoardListDto(condition, pageable);

        //then
        assertThat(result.getContent().size()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("게시물 10개, searchTarget=내용&searchTerm=content")
    void boardListDto8() {
        //given
        Member member = new Member("member", "email", "password");
        memberService.join(member);

        for (int i = 0; i < 10; i++) {
            boardService.saveBoard(member.getId(), "title" + i, "content" + i, new ArrayList<>());
        }

        PageRequest pageable = PageRequest.of(0, 5);
        BoardSearchCondition condition = new BoardSearchCondition();
        condition.setSearchTarget(SearchTarget.CONTENT);
        String searchTerm = "content";
        condition.setSearchTerm(searchTerm);

        //when
        Page<BoardListDto> result = boardQueryService.findBoardListDto(condition, pageable);

        //then
        assertThat(result.getContent().size()).isEqualTo(5);
        assertThat(result.getTotalElements()).isEqualTo(10);
    }

    @Test
    @DisplayName("게시물X, searchTarget=작성자&searchTerm=")
    void boardListDto9() {
        //given
        PageRequest pageable = PageRequest.of(0, 5);
        BoardSearchCondition condition = new BoardSearchCondition();
        condition.setSearchTarget(SearchTarget.NAME);
        condition.setSearchTerm("");

        //when
        Page<BoardListDto> result = boardQueryService.findBoardListDto(condition, pageable);

        //then
        assertThat(result.getContent().size()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("게시물 10개, searchTarget=작성자&searchTerm=member")
    void boardListDto10() {
        //given
        Member member = new Member("member", "email", "password");
        memberService.join(member);

        for (int i = 0; i < 10; i++) {
            boardService.saveBoard(member.getId(), "title" + i, "content" + i, new ArrayList<>());
        }

        PageRequest pageable = PageRequest.of(0, 5);
        BoardSearchCondition condition = new BoardSearchCondition();
        condition.setSearchTarget(SearchTarget.NAME);
        String searchTerm = "member";
        condition.setSearchTerm(searchTerm);

        //when
        Page<BoardListDto> result = boardQueryService.findBoardListDto(condition, pageable);

        //then
        assertThat(result.getContent().size()).isEqualTo(5);
        assertThat(result.getTotalElements()).isEqualTo(10);
    }

    @Test
    @DisplayName("게시물 10개, searchTarget=작성자&searchTerm=m")
    void boardListDto11() {
        //given
        Member member = new Member("member", "email", "password");
        memberService.join(member);

        for (int i = 0; i < 10; i++) {
            boardService.saveBoard(member.getId(), "title" + i, "content" + i, new ArrayList<>());
        }

        PageRequest pageable = PageRequest.of(0, 5);
        BoardSearchCondition condition = new BoardSearchCondition();
        condition.setSearchTarget(SearchTarget.NAME);
        String searchTerm = "a";
        condition.setSearchTerm(searchTerm);

        //when
        Page<BoardListDto> result = boardQueryService.findBoardListDto(condition, pageable);

        //then
        assertThat(result.getContent().size()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("게시물X, searchTarget=작성자&searchTerm=검색어")
    void boardListDto12() {
        //given
        PageRequest pageable = PageRequest.of(0, 5);
        BoardSearchCondition condition = new BoardSearchCondition();
        condition.setSearchTarget(SearchTarget.NAME);
        condition.setSearchTerm("검색어");

        //when
        Page<BoardListDto> result = boardQueryService.findBoardListDto(condition, pageable);

        //then
        assertThat(result.getContent().size()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("게시물 10개, searchTarget=&searchTerm=&sort=최신순")
    void boardListDto13() {
        //given
        Member member = new Member("member", "email", "password");
        memberService.join(member);

        for (int i = 0; i < 10; i++) {
            boardService.saveBoard(member.getId(), "title" + i, "content" + i, new ArrayList<>());
        }

        PageRequest pageable = PageRequest.of(1, 2);
        BoardSearchCondition condition = new BoardSearchCondition();
        condition.setSearchTarget(SearchTarget.NAME);
        String searchTerm = "member";
        condition.setSearchTerm(searchTerm);
        condition.setSortBy(SearchSort.NEWEST);

        //when
        Page<BoardListDto> result = boardQueryService.findBoardListDto(condition, pageable);

        //then
        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(10);
        assertThat(result.getContent())
                .extracting("title")
                .containsExactly("title7", "title6");
    }

    @Test
    @DisplayName("게시물 10개, searchTarget=&searchTerm=&sort=오래된순")
    void boardListDto14() {
        //given
        Member member = new Member("member", "email", "password");
        memberService.join(member);

        for (int i = 0; i < 10; i++) {
            boardService.saveBoard(member.getId(), "title" + i, "content" + i, new ArrayList<>());
        }

        PageRequest pageable = PageRequest.of(1, 2);
        BoardSearchCondition condition = new BoardSearchCondition();
        condition.setSearchTarget(SearchTarget.NAME);
        String searchTerm = "member";
        condition.setSearchTerm(searchTerm);
        condition.setSortBy(SearchSort.OLDEST);

        //when
        Page<BoardListDto> result = boardQueryService.findBoardListDto(condition, pageable);

        //then
        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.getTotalElements()).isEqualTo(10);
        assertThat(result.getContent())
                .extracting("title")
                .containsExactly("title2", "title3");
    }

    @Test
    @DisplayName("권한 있는 사용자가 조회한 경우")
    void findBoardUpdateDto1() {
        //given
        Member member = new Member("member1", "email1", "password1");
        memberService.join(member);

        Board savedBoard = boardService.saveBoard(member.getId(), "title1", "content1", new ArrayList<>());

        //when
        BoardUpdateDto result = boardQueryService.findBoardUpdateDto(savedBoard.getId(), member.getId());

        //then
        assertThat(result.getBoardId()).isEqualTo(savedBoard.getId());
        assertThat(result.getTitle()).isEqualTo(savedBoard.getTitle());
        assertThat(result.getContent()).isEqualTo(savedBoard.getContent());
        assertThat(result.getUploadFileListDto().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("권한 없는 사용자가 조회한 경우 - memberId 불일치")
    void findBoardUpdateDto2() {
        //given
        Member member1 = new Member("member1", "email1", "password1");
        memberService.join(member1);
        Member member2 = new Member("member2", "email2", "password2");
        memberService.join(member2);

        Board savedBoard = boardService.saveBoard(member1.getId(), "title1", "content1", new ArrayList<>());

        //when then
        assertThatThrownBy(() -> boardQueryService.findBoardUpdateDto(savedBoard.getId(), member2.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("권한 없는 사용자가 조회한 경우 - boardId 불일치")
    void findBoardUpdateDto3() {
        //given
        Member member1 = new Member("member1", "email1", "password1");
        memberService.join(member1);
        Member member2 = new Member("member2", "email2", "password2");
        memberService.join(member2);

        Board savedBoard1 = boardService
                .saveBoard(member1.getId(), "title1", "content1", new ArrayList<>());
        Board savedBoard2 = boardService
                .saveBoard(member2.getId(), "title2", "content2", new ArrayList<>());

        //when then
        assertThatThrownBy(() -> boardQueryService.findBoardUpdateDto(savedBoard2.getId(), member1.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("존재하지 않는 게시물조회")
    void findBoardUpdateDto4() {
        //given
        Member member1 = new Member("member1", "email1", "password1");
        memberService.join(member1);
        Member member2 = new Member("member2", "email2", "password2");
        memberService.join(member2);

        Board savedBoard1 = boardService
                .saveBoard(member1.getId(), "title1", "content1", new ArrayList<>());
        Board savedBoard2 = boardService
                .saveBoard(member2.getId(), "title2", "content2", new ArrayList<>());

        //when then
        assertThatThrownBy(() -> boardQueryService.findBoardUpdateDto(savedBoard2.getId() + 1, member1.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("여레개의 게시물이 존재할때에 성공적으로 찾는 경우")
    void findBoardUpdateDto5() {
        //given
        Member member1 = new Member("member1", "email1", "password1");
        memberService.join(member1);
        Member member2 = new Member("member2", "email2", "password2");
        memberService.join(member2);

        //when
        Board savedBoard1 = boardService
                .saveBoard(member1.getId(), "title1", "content1", new ArrayList<>());
        Board savedBoard2 = boardService
                .saveBoard(member2.getId(), "title2", "content2", new ArrayList<>());

        //then
        BoardUpdateDto result = boardQueryService.findBoardUpdateDto(savedBoard2.getId(), member2.getId());
        assertThat(result.getBoardId()).isEqualTo(savedBoard2.getId());
        assertThat(result.getTitle()).isEqualTo(savedBoard2.getTitle());
        assertThat(result.getContent()).isEqualTo(savedBoard2.getContent());
        assertThat(result.getUploadFileListDto().size()).isEqualTo(0);
    }
}