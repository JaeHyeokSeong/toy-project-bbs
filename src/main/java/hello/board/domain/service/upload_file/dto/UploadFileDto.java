package hello.board.domain.service.upload_file.dto;

import hello.board.entity.UploadFile;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UploadFileDto {
    private Long id;
    private String originalFileName; //원본 파일 이름
    private String storeFileName; //서버에 저장되어질 파일 이름
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public UploadFileDto(UploadFile uploadFile) {
        id = uploadFile.getId();
        originalFileName = uploadFile.getOriginalFileName();
        storeFileName = uploadFile.getStoreFileName();
        createdDate = uploadFile.getCreatedDate();
        lastModifiedDate = uploadFile.getLastModifiedDate();
    }
}
