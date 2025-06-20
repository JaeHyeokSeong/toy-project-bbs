package hello.board.api.board_reaction;

import hello.board.SessionConst;
import hello.board.api.board_reaction.dto.BoardReactionDto;
import hello.board.api.board_reaction.dto.BoardReactionResultDto;
import hello.board.domain.service.board_reaction.BoardReactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board-reaction")
public class BoardReactionApiController {

    private final BoardReactionService boardReactionService;

    @PostMapping("/{boardId}")
    public BoardReactionResultDto boardReactionDto(
            @Valid @RequestBody BoardReactionDto boardReactionDto, BindingResult bindingResult,
            @PathVariable Long boardId,
            @SessionAttribute(SessionConst.MEMBER_ID) Long memberId) {

        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("전달된 ReactionType이 없습니다.");
        }

        boardReactionService.reflectReaction(boardId, memberId, boardReactionDto.getReactionType());

        long count = boardReactionService.totalReactionCount(boardId);
        return new BoardReactionResultDto(boardId, count);
    }
}
