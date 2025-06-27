package hello.board.api.board_reaction;

import hello.board.SessionConst;
import hello.board.api.board_reaction.dto.AddBoardReactionDto;
import hello.board.api.board_reaction.dto.AddBoardReactionResultDto;
import hello.board.exception.BindingResultException;
import hello.board.service.board_reaction.BoardReactionService;
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
    public AddBoardReactionResultDto boardReactionDto(
            @Valid @RequestBody AddBoardReactionDto addBoardReactionDto, BindingResult bindingResult,
            @PathVariable Long boardId,
            @SessionAttribute(SessionConst.MEMBER_ID) Long memberId) {

        if (bindingResult.hasErrors()) {
            throw new BindingResultException(bindingResult.getAllErrors());
        }

        boardReactionService.reflectReaction(boardId, memberId, addBoardReactionDto.getReactionType());

        long count = boardReactionService.totalReactionCount(boardId);
        return new AddBoardReactionResultDto(boardId, count);
    }
}
