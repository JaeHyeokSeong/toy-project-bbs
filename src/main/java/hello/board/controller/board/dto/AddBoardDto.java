package hello.board.controller.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
public class AddBoardDto {
    @NotBlank
    @Length(max = 100)
    private String title;

    @NotBlank
    private String content;

    @Size(max = 20)
    private List<String> storeFileNames;
}
