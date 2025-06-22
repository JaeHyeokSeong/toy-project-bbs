package hello.board.api.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentParentTotalCountDto {
    Long boardId;
    Long totalCount;
}
