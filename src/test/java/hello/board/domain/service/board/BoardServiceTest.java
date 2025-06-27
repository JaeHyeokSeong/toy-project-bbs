package hello.board.domain.service.board;

import hello.board.domain.repository.board.BoardRepository;
import hello.board.domain.repository.board.query.dto.BoardQueryDto;
import hello.board.domain.repository.comment.CommentRepository;
import hello.board.domain.repository.upload_file.UploadFileRepository;
import hello.board.domain.service.board.query.BoardQueryService;
import hello.board.domain.service.member.MemberService;
import hello.board.entity.board.Board;
import hello.board.entity.comment.Comment;
import hello.board.entity.file.UploadFile;
import hello.board.entity.member.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class BoardServiceTest {

    @Autowired
    BoardService boardService;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    BoardQueryService boardQueryService;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UploadFileRepository uploadFileRepository;

    Member member;

    @BeforeEach
    void init() {
        member = new Member("memberA", "email", "password");
        memberService.join(member);
    }

    @Test
    void 게시물_저장() {
        //given
        String title = "titleA";
        String content = "contentA";

        //when
        Board savedBoard = boardService.saveBoard(member.getId(), title, content, new ArrayList<>());

        //then
        Optional<Board> findBoard = boardRepository.findBoard(savedBoard.getId(), member.getId());
        assertThat(findBoard).isPresent();
        Board board = findBoard.get();
        assertThat(board.getTitle()).isEqualTo(title);
        assertThat(board.getContent()).isEqualTo(content);
        assertThat(board.getViews()).isEqualTo(0);
        assertThat(board.getUploadFiles().size()).isEqualTo(0);
    }

    @Test
    void 게시물_수정() {
        //given
        ArrayList<UploadFile> uploadFiles = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            uploadFiles.add(new UploadFile("oldOFN" + i, "oldSFN" + i));
        }

        Board savedBoard = boardService.saveBoard(
                member.getId(),
                "oldTitle",
                "oldContent",
                uploadFiles
        );

        //when
        ArrayList<UploadFile> newUploadFiles = new ArrayList<>();
        newUploadFiles.add(new UploadFile("newOFN", "newSFN"));

        String slug = boardService.updateBoard(
                savedBoard.getId(),
                member.getId(),
                "newTitle",
                "newContent",
                newUploadFiles,
                true
        );

        //then
        Optional<BoardQueryDto> findBoardQueryDto = boardQueryService
                .findBoardQueryDto(savedBoard.getId(), slug, 0, null);
        assertThat(findBoardQueryDto.isPresent()).isTrue();
        BoardQueryDto boardQueryDto = findBoardQueryDto.get();

        assertThat(boardQueryDto.getBoardTitle()).isEqualTo("newTitle");
        assertThat(boardQueryDto.getBoardContent()).isEqualTo("newContent");
        assertThat(boardQueryDto.getUploadedFiles().size()).isEqualTo(1);
        assertThat(boardQueryDto.getUploadedFiles()).extracting("originalFileName")
                .containsExactly("newOFN");
        assertThat(boardQueryDto.getUploadedFiles()).extracting("storeFileName")
                .containsExactly("newSFN");
    }

    @Test
    void 게시물_수정_권한없음() {
        //given
        ArrayList<UploadFile> uploadFiles = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            uploadFiles.add(new UploadFile("oldOFN" + i, "oldSFN" + i));
        }

        Board savedBoard = boardService.saveBoard(
                member.getId(),
                "oldTitle",
                "oldContent",
                uploadFiles
        );

        //when then
        ArrayList<UploadFile> newUploadFiles = new ArrayList<>();
        newUploadFiles.add(new UploadFile("newOFN", "newSFN"));

        assertThatThrownBy(() -> boardService.updateBoard(
                savedBoard.getId(),
                member.getId() + 1,
                "newTitle",
                "newContent",
                newUploadFiles,
                true
        )).isInstanceOf(NoSuchElementException.class);
    }


    @Test
    void 게시물_삭제() {
        //given
        ArrayList<UploadFile> uploadFiles = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            uploadFiles.add(new UploadFile("oldOFN" + i, "oldSFN" + i));
        }

        Board savedBoard = boardService.saveBoard(
                member.getId(),
                "oldTitle",
                "oldContent",
                uploadFiles
        );

        //when
        boardService.deleteBoard(savedBoard.getId(), member.getId());

        //then
        Optional<Board> board = boardRepository.findById(savedBoard.getId());
        assertThat(board).isEmpty();
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments.size()).isEqualTo(0);
        List<UploadFile> findUploadFiles = uploadFileRepository.findAll();
        assertThat(findUploadFiles.size()).isEqualTo(0);
    }
}