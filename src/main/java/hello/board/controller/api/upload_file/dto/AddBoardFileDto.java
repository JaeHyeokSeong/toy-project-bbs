package hello.board.controller.api.upload_file.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AddBoardFileDto {

    @NotNull
    private MultipartFile multipartFile;
}
