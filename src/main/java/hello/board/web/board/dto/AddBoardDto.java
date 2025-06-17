package hello.board.web.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class AddBoardDto {
    @NotBlank
    @Length(max = 100)
    private String title;
    @NotBlank
    private String content;
    private List<MultipartFile> files;
}
