package hello.board.api.board_reaction.dto;

import lombok.Data;

@Data
public class AddBoardReactionResultDto {

    private Long boardId;
    private long totalReactionCounts;

    public AddBoardReactionResultDto(Long boardId, long totalReactionCounts) {
        this.boardId = boardId;
        this.totalReactionCounts = totalReactionCounts;
    }
}
