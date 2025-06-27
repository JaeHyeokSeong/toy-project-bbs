package hello.board.service.board.query.dto;

import hello.board.repository.upload_file.dto.UploadFileDto;
import hello.board.entity.board.Board;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

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
    private List<UploadFileDto> uploadedFiles;
    private List<MultipartFile> multipartFiles;
    private boolean updateFile;

    public BoardUpdateDto(Board board) {
        boardId = board.getId();
        slug = board.getSlug();
        title = board.getTitle();
        content = board.getContent();
        uploadedFiles = board.getUploadFiles()
                .stream()
                .map(UploadFileDto::new)
                .toList();
    }
}
