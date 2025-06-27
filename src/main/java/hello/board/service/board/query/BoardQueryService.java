package hello.board.service.board.query;

import hello.board.repository.board.BoardRepository;
import hello.board.repository.board.query.BoardQueryRepository;
import hello.board.repository.board.query.dto.BoardQueryDto;
import hello.board.repository.board.query.dto.BoardSearchCondition;
import hello.board.service.board.query.dto.BoardListDto;
import hello.board.service.board.query.dto.BoardUpdateDto;
import hello.board.entity.board.Board;
import hello.board.exception.BoardNotFoundException;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardQueryService {

    private final BoardRepository boardRepository;
    private final BoardQueryRepository boardQueryRepository;

    @Transactional
    public Optional<BoardQueryDto> findBoardQueryDto(Long boardId, String slug, int count, @Nullable Long memberId) {
        if (count > 0) {
            Board board = boardRepository.findByIdAndSlug(boardId, slug).orElseThrow(BoardNotFoundException::new);
            board.increaseView(count);
        }
        return boardQueryRepository.findBoardQueryDto(boardId, slug, memberId);
    }

    public Page<BoardListDto> findBoardListDto(BoardSearchCondition condition, Pageable pageable) {
        Page<Board> result = boardQueryRepository.findAllBoards(condition, pageable);
        return result.map(BoardListDto::new);
    }

    /**
     * @throws java.util.NoSuchElementException 게시물을 못찾은 경우
     */
    public BoardUpdateDto findBoardUpdateDto(Long boardId, Long memberId) {
        Board board = boardRepository.findBoard(boardId, memberId).orElseThrow(BoardNotFoundException::new);
        return new BoardUpdateDto(board);
    }
}
