package hello.board.controller.api.comment.dto;

import lombok.Data;

@Data
public class DeleteCommentResultDto {
    private Long commentId;
    private String message;

    public DeleteCommentResultDto(Long commentId, String message) {
        this.commentId = commentId;
        this.message = message;
    }
}
