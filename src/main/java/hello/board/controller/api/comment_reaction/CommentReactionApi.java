package hello.board.controller.api.comment_reaction;

import hello.board.SessionConst;
import hello.board.controller.api.comment_reaction.dto.AddCommentReactionDto;
import hello.board.controller.api.comment_reaction.dto.AddCommentReactionResultDto;
import hello.board.service.comment_reaction.CommentReactionService;
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
    public AddCommentReactionResultDto commentReaction(@PathVariable Long commentId,
                                                       @SessionAttribute(SessionConst.MEMBER_ID) Long memberId,
                                                       @Valid @RequestBody AddCommentReactionDto dto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BindingResultException(bindingResult.getAllErrors());
        }

        commentReactionService.reflectReaction(commentId, memberId, dto.getReactionType());
        long totalReactionCount = commentReactionService.totalReactionCount(commentId);

        return new AddCommentReactionResultDto(commentId, totalReactionCount);
    }
}
