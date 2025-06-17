package hello.board.domain.service.board.query.dto;

import hello.board.domain.service.upload_file.dto.UploadFileDto;
import hello.board.entity.Board;
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
    @NotBlank
    @Length(max = 100)
    private String title;
    @NotBlank
    private String content;
    private List<UploadFileDto> uploadFileListDto;
    private List<MultipartFile> multipartFiles;
    private boolean updateFile;

    public BoardUpdateDto(Board board) {
        boardId = board.getId();
        title = board.getTitle();
        content = board.getContent();
        uploadFileListDto = board.getUploadFiles()
                .stream()
                .map(UploadFileDto::new)
                .toList();
    }
}
