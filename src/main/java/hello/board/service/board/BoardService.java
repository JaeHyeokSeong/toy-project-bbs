package hello.board.service.board;

import hello.board.repository.board.BoardRepository;
import hello.board.repository.board_reaction.BoardReactionRepository;
import hello.board.repository.comment.CommentRepository;
import hello.board.repository.comment_reaction.CommentReactionRepository;
import hello.board.repository.upload_file.UploadFileRepository;
import hello.board.service.member.MemberService;
import hello.board.entity.board.Board;
import hello.board.entity.member.Member;
import hello.board.entity.file.UploadFile;
import hello.board.exception.BoardNotFoundException;
import hello.board.exception.MemberNotFoundException;
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
    private final CommentReactionRepository commentReactionRepository;

    /**
     * 게시물 저장
     */
    public Board saveBoard(Long memberId, String title,
                          String content, @NotNull List<String> storeFileNames) {

        Member findMember = memberService.findById(memberId).orElseThrow(MemberNotFoundException::new);

        List<UploadFile> uploadFileList = uploadFileRepository.findAllByStoreFileNames(storeFileNames);
        Board board = Board.createBoard(title, content, findMember, uploadFileList);

        return boardRepository.save(board);
    }

    /**
     *  게시물 수정
     */
    public String updateBoard(Long boardId, Long memberId, String title, String content,
                            List<String> storeFileNames, List<String> deleteFileNames) {

        //접근 권한 확인
        boardRepository.findBoard(boardId, memberId).orElseThrow(
                () -> new BoardNotFoundException("존재하지 않는 게시물 입니다. 전달된 boardId=" + boardId));

        if (!deleteFileNames.isEmpty()) {
            uploadFileRepository.deleteAllByStoreFileNames(deleteFileNames);
        }

        //새로운 영속성 컨텍스트 시작
        Board board = boardRepository.findById(boardId).orElseThrow();

        if (!storeFileNames.isEmpty()) {
            List<UploadFile> uploadFileList = uploadFileRepository.findAllByStoreFileNames(storeFileNames);
            for (UploadFile uploadFile : uploadFileList) {
                uploadFile.setBoard(board);
            }
        }

        board.changeTitle(title);
        board.changeContent(content);

        return board.getSlug();
    }

    /**
     *  게시물 삭제
     */
    public void deleteBoard(Long boardId, Long memberId) {

        //권한 체크
        boardRepository.findBoard(boardId, memberId).orElseThrow(BoardNotFoundException::new);

        //파일 모두 삭제하기 (벌크연산)
        uploadFileRepository.deleteAllUploadFiles(boardId);

        //게시물에 달린 모든 comment들에 대한 모든 reaction들 삭제하기
        commentReactionRepository.deleteAllByBoardId(boardId);

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
