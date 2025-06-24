package hello.board.api.comment.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCommentDto {

    //comment 정보
    @NotBlank
    private String content;
    @Nullable
    private Long parentCommentId;
}
