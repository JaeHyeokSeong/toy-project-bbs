package hello.board.api.comment_reaction;

import hello.board.SessionConst;
import hello.board.api.comment_reaction.dto.CommentReactionDto;
import hello.board.api.comment_reaction.dto.CommentReactionResultDto;
import hello.board.domain.service.comment_reaction.CommentReactionService;
import hello.board.exception.BindingResultException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment-reaction")
public class CommentReactionApi {

    private final CommentReactionService commentReactionService;

    @PostMapping("/{commentId}")
    public CommentReactionResultDto commentReaction(@PathVariable Long commentId,
                                                    @SessionAttribute(SessionConst.MEMBER_ID) Long memberId,
                                                    @Valid @RequestBody CommentReactionDto dto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BindingResultException(bindingResult.getAllErrors());
        }

        commentReactionService.reflectReaction(commentId, memberId, dto.getReactionType());
        long totalReactionCount = commentReactionService.totalReactionCount(commentId);

        return new CommentReactionResultDto(commentId, totalReactionCount);
    }
}
