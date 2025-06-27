package hello.board.controller.api.comment_reaction.dto;

import hello.board.entity.ReactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCommentReactionDto {

    @NotNull
    private ReactionType reactionType;
}
