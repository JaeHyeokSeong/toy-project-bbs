package hello.board.repository.member.query.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberThumbnailFileQueryDto {

    private Long memberThumbnailFileId;
    private String originalFileName;
    private String storeFileName;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private Long memberId;
}
