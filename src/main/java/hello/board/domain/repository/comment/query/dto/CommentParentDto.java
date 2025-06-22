package hello.board.domain.repository.comment.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentParentDto {

    //답변 정보
    private Long commentId;
    private String content;
    private LocalDateTime createdDate;

    //게시물 정보
    private Long boardId;

    //작성자 정보
    private String name;
}
