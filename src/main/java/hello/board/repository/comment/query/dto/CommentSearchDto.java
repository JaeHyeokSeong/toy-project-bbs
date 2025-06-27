package hello.board.repository.comment.query.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentSearchDto {
    @Nullable
    private Long parentCommentId;
    @NotNull
    private CommentSearchSort searchSort;

    public CommentSearchDto(@Nullable Long parentCommentId, CommentSearchSort searchSort) {
        this.parentCommentId = parentCommentId;
        this.searchSort = searchSort;
    }
}
