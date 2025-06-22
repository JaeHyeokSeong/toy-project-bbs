package hello.board.api.comment.dto;

import hello.board.entity.Comment;
import lombok.Data;

@Data
public class AddCommentParentResponseDto {
    private Long commentId;
    private Long boardId;
    private Long memberId;
    private String content;

    public AddCommentParentResponseDto(Comment comment) {
        this.commentId = comment.getId();
        this.boardId = comment.getBoard().getId();
        this.memberId = comment.getMember().getId();
        this.content = comment.getContent();
    }
}
