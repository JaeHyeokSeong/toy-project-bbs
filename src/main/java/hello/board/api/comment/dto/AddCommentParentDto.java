package hello.board.api.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCommentParentDto {

    @NotBlank
    private String content;
}
