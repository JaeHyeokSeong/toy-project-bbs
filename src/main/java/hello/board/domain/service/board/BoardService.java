package hello.board.domain.service.board;

import hello.board.domain.repository.board.BoardRepository;
import hello.board.domain.repository.board_reaction.BoardReactionRepository;
import hello.board.domain.repository.comment.CommentRepository;
import hello.board.domain.repository.upload_file.UploadFileRepository;
import hello.board.domain.service.member.MemberService;
import hello.board.entity.board.Board;
import hello.board.entity.member.Member;
import hello.board.entity.file.UploadFile;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final MemberService memberService;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final UploadFileRepository uploadFileRepository;
    private final BoardReactionRepository boardReactionRepository;

    /**
     * 게시물 저장
     */
    public Board saveBoard(Long memberId, String title,
                          String content, @NotNull List<UploadFile> uploadFiles) {

        Member findMember = memberService.findById(memberId).orElseThrow();
        Board board = Board.createBoard(title, content, findMember, uploadFiles);
        return boardRepository.save(board);
    }

    /**
     *  게시물 수정
     */
    public String updateBoard(Long boardId, Long memberId, String title, String content,
                            List<UploadFile> newUploadFiles, boolean updateFile) {

        //접근 권한 확인
        boardRepository.findBoard(boardId, memberId).orElseThrow();

        if (updateFile) {
            //현재 등록되어진 모든 첨부 파일들 삭제 (벌크연산)
            uploadFileRepository.deleteAllUploadFiles(boardId);
        }

        //새로운 영속성 컨텍스트 시작
        Board board = boardRepository.findById(boardId).orElseThrow();

        board.changeTitle(title);
        board.changeContent(content);
        if (updateFile) {
            //새로운 첨부 파일들 등록
            for (UploadFile newFile : newUploadFiles) {
                board.addUploadFile(newFile);
            }
        }

        return board.getSlug();
    }

    /**
     *  게시물 삭제
     */
    public void deleteBoard(Long boardId, Long memberId) {

        //권한 체크
        boardRepository.findBoard(boardId, memberId).orElseThrow();

        //파일 모두 삭제하기 (벌크연산)
        uploadFileRepository.deleteAllUploadFiles(boardId);

        //게시물에 달린 댓글 삭제하기 (벌크연산)
        commentRepository.deleteAllByBoardId(boardId);

        //게시물에 달린 좋아요, 싫어요 삭제하기 (벌크연산)
        boardReactionRepository.deleteAllBoardReaction(boardId);

        //게시물 작세하기
        boardRepository.deleteById(boardId);
    }

    public Optional<Board> findById(Long boardId) {
        return boardRepository.findById(boardId);
    }
}
