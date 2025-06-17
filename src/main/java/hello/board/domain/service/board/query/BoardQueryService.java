package hello.board.domain.service.board.query;

import hello.board.domain.repository.board.BoardRepository;
import hello.board.domain.repository.board.query.BoardQueryRepository;
import hello.board.domain.repository.board.query.dto.BoardSearchCondition;
import hello.board.domain.service.board.query.dto.BoardDto;
import hello.board.domain.service.board.query.dto.BoardListDto;
import hello.board.domain.service.board.query.dto.BoardUpdateDto;
import hello.board.entity.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardQueryService {

    private final BoardRepository boardRepository;
    private final BoardQueryRepository boardQueryRepository;

    /**
     * @param id 게시물 아이디
     * @param slug 게시물 아이디
     * @throws java.util.NoSuchElementException 게시물을 못찾은 경우
     */
    @Transactional
    public BoardDto findBoardDto(Long id, String slug, int count) {
        Board board = boardRepository.findByIdAndSlug(id, slug).orElseThrow();
        if (count > 0) {
            board.increaseView(count);
        }
        return new BoardDto(board);
    }

    public Page<BoardListDto> findBoardListDto(BoardSearchCondition condition, Pageable pageable) {
        Page<Board> result = boardQueryRepository.findAllBoards(condition, pageable);
        return result.map(BoardListDto::new);
    }

    /**
     * @throws java.util.NoSuchElementException 게시물을 못찾은 경우
     */
    public BoardUpdateDto findBoardUpdateDto(Long boardId, Long memberId) {
        Board board = boardRepository.findBoard(boardId, memberId).orElseThrow();
        return new BoardUpdateDto(board);
    }
}
