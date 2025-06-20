package hello.board.domain.service.board.query;

import hello.board.domain.repository.board.BoardRepository;
import hello.board.domain.repository.board.query.BoardQueryRepository;
import hello.board.domain.repository.board.query.dto.BoardSearchCondition;
import hello.board.domain.service.board.query.dto.BoardDto;
import hello.board.domain.service.board.query.dto.BoardListDto;
import hello.board.domain.service.board.query.dto.BoardUpdateDto;
import hello.board.domain.service.board_reaction.BoardReactionService;
import hello.board.entity.Board;
import hello.board.entity.ReactionType;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardQueryService {

    private final BoardReactionService boardReactionService;
    private final BoardRepository boardRepository;
    private final BoardQueryRepository boardQueryRepository;

    /**
     * @param id 게시물 아이디
     * @param slug 게시물 아이디
     * @throws java.util.NoSuchElementException 게시물을 못찾은 경우
     */
    @Transactional
    public BoardDto findBoardDto(Long id, String slug, int count, @Nullable Long memberId) {
        Board board = boardRepository.findByIdAndSlug(id, slug).orElseThrow();
        if (count > 0) {
            board.increaseView(count);
        }

        BoardDto boardDto = new BoardDto(board);

        //게시물 전체 reaction수 가지고 오기
        long totalReactionCount = boardReactionService.totalReactionCount(id);
        boardDto.setTotalReactionCount(totalReactionCount);

        //사용자가 이 게시물에 대해서 어떠한 반응을 했는지 가져오기
        if (memberId != null) {
            ReactionType reactionType = boardReactionService.findReaction(id, memberId);
            boardDto.setReactionType(reactionType);
        }

        return boardDto;
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
