package hello.board.api.board_reaction.dto;

import lombok.Data;

@Data
public class BoardReactionResultDto {

    private Long boardId;
    private long totalReactionCounts;

    public BoardReactionResultDto(Long boardId, long totalReactionCounts) {
        this.boardId = boardId;
        this.totalReactionCounts = totalReactionCounts;
    }
}
