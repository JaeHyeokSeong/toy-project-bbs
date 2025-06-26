package hello.board.api.comment_reaction.dto;

import hello.board.entity.ReactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentReactionDto {

    @NotNull
    private ReactionType reactionType;
}
