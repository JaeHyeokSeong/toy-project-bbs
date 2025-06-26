package hello.board.domain.service.comment.dto;

import hello.board.entity.comment.Comment;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class UpdateCommentResultDto {
    //comment 정보
    private Long commentId;
    private String content;
    private LocalDateTime lastModifiedDate;
    private Long parentCommentId;

    //member 정보
    private Long memberId;

    public UpdateCommentResultDto(Comment comment) {
        commentId = comment.getId();
        content = comment.getContent();
        lastModifiedDate = comment.getLastModifiedDate();
        parentCommentId = comment.getParentComment() == null ? null : comment.getParentComment().getId();
        memberId = comment.getMember().getId();
    }
}
