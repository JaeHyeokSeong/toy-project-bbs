package hello.board.domain.repository.comment.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentParentDto {

    private Long commentId;
    private Long boardId;
    private Long memberId;
    private LocalDateTime createdDate;
    private String content;
}
