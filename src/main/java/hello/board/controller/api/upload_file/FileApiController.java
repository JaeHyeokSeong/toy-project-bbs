package hello.board.controller.api.upload_file;

import hello.board.dto.ResponseResult;
import hello.board.entity.FileStore;
import hello.board.entity.board.UploadFile;
import hello.board.exception.EmptyFileException;
import hello.board.repository.upload_file.dto.UploadFileDto;
import hello.board.service.upload_file.UploadFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileApiController {

    private final UploadFileService uploadFileService;
    private final FileStore fileStore;

    @PostMapping("/file")
    public ResponseEntity<ResponseResult> uploadImage(@RequestParam MultipartFile multipartFile) {

        String storeFileName = fileStore.storeFile(multipartFile)
                .orElseThrow(() -> new EmptyFileException("파일저장 실패, 전달된 파일이 없습니다."));

        UploadFileDto uploadFileDto = uploadFileService
                .saveUploadFile(new UploadFile(multipartFile.getOriginalFilename(), storeFileName));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .cacheControl(CacheControl.noCache())
                .lastModified(uploadFileDto.getLastModifiedDate().atZone(ZoneId.of("Asia/Seoul")))
                .body(new ResponseResult(HttpStatus.CREATED.toString(), "게시물 파일 업로드 완료.", uploadFileDto));
    }

    @GetMapping("/files/{storeFileName}")
    public ResponseEntity<Resource> image(@PathVariable String storeFileName) throws IOException {
        UploadFileDto uploadFileDto = uploadFileService.findUploadFileDto(storeFileName);
        UrlResource content = new UrlResource("file:" + fileStore.getFullPath(uploadFileDto.getStoreFileName()));

        CacheControl cacheControl = CacheControl.noCache();

        //content-type 찾기
        String contentType = Files.probeContentType(Paths.get(fileStore.getFullPath(uploadFileDto.getStoreFileName())));

        ResponseEntity.BodyBuilder builder = ResponseEntity.ok();

        //content-type 설정하기
        if (contentType != null) {
            builder.contentType(MediaType.valueOf(contentType));
        }

        return builder
                .cacheControl(cacheControl)
                .lastModified(uploadFileDto.getLastModifiedDate().atZone(ZoneId.of("Asia/Seoul")))
                .body(content);
    }
}
