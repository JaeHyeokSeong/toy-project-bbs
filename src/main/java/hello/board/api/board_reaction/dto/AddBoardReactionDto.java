package hello.board.api.board_reaction.dto;

import hello.board.entity.ReactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddBoardReactionDto {

    @NotNull
    private ReactionType reactionType;
}
