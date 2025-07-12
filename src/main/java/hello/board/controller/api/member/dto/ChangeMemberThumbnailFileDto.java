package hello.board.controller.api.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ChangeMemberThumbnailFileDto {

    @NotNull
    private MultipartFile multipartFile;
}
