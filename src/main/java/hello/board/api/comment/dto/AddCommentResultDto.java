package hello.board.api.comment.dto;

import hello.board.entity.comment.Comment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddCommentResultDto {
    //답변 정보
    private Long commentId;
    private String content;
    private LocalDateTime createdDate;

    //게시물 정보
    private Long boardId;

    //사용자 정보
    private Long memberId;

    public AddCommentResultDto(Comment comment) {
        commentId = comment.getId();
        content = comment.getContent();
        createdDate = comment.getCreatedDate();
        boardId = comment.getBoard().getId();
        memberId = comment.getMember().getId();
    }
}
