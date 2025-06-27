package hello.board.api.comment_reaction.dto;

import lombok.Data;

@Data
public class AddCommentReactionResultDto {

    private Long commentId;
    private Long totalReactionCounts;

    public AddCommentReactionResultDto(Long commentId, Long totalReactionCounts) {
        this.commentId = commentId;
        this.totalReactionCounts = totalReactionCounts;
    }
}
