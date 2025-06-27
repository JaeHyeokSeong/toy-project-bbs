package hello.board.repository.upload_file.query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UploadFileQueryDto {
    private Long uploadFileId;
    private String originalFileName; //원본 파일 이름
    private String storeFileName; //서버에 저장되어질 파일 이름
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
