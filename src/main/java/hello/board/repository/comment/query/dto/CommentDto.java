package hello.board.repository.comment.query.dto;

import hello.board.entity.ReactionType;
import hello.board.entity.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {

    //게시물 정보
    private Long boardId;

    //답변 정보
    private Long commentId;
    private Long parentCommentId;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private Boolean isEditable; //수정/삭제 권환
    private long totalLikesPlusTotalDislikes;
    private long totalChildComments;
    private ReactionType reactionType;

    //작성자 정보
    private String writerName;
    private RoleType writerRole;
}
