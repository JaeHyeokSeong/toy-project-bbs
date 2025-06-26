package hello.board.api.comment_reaction.dto;

import lombok.Data;

@Data
public class CommentReactionResultDto {

    private Long commentId;
    private Long totalReactionCounts;

    public CommentReactionResultDto(Long commentId, Long totalReactionCounts) {
        this.commentId = commentId;
        this.totalReactionCounts = totalReactionCounts;
    }
}
