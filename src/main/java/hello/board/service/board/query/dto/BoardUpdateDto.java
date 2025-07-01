package hello.board.service.board.query.dto;

import hello.board.entity.board.Board;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@NoArgsConstructor
public class BoardUpdateDto {
    private Long boardId;
    private String slug;
    @NotBlank
    @Length(max = 100)
    private String title;
    @NotBlank
    private String content;
    @Size(max = 20)
    private List<String> storeFileNames;
    private List<String> deleteFileNames;

    public BoardUpdateDto(Board board) {
        boardId = board.getId();
        slug = board.getSlug();
        title = board.getTitle();
        content = board.getContent();
    }
}
