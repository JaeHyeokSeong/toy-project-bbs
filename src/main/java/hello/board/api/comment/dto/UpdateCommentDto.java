package hello.board.api.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCommentDto {
    @NotNull
    private Long commentId;
    @NotBlank
    private String content;
}
