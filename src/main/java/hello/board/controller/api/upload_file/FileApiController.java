package hello.board.controller.api.upload_file;

import hello.board.entity.file.UploadFile;
import hello.board.service.upload_file.UploadFileService;
import hello.board.repository.upload_file.dto.UploadFileDto;
import hello.board.entity.file.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileApiController {

    private final UploadFileService uploadFileService;
    private final FileStore fileStore;

    @PostMapping("/image")
    public ResponseEntity<UploadFileDto> uploadImage(@RequestParam MultipartFile multipartFile) {
        UploadFile uploadFile = fileStore.storeFile(multipartFile);
        UploadFileDto uploadFileDto = uploadFileService.saveUploadFile(uploadFile);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noCache())
                .lastModified(uploadFileDto.getLastModifiedDate().atZone(ZoneId.of("Asia/Seoul")))
                .body(uploadFileDto);
    }

    @GetMapping("/images/{storeFileName}")
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

    @GetMapping("/files/{storeFileName}")
    public ResponseEntity<Resource> file(@PathVariable String storeFileName) throws IOException {
        UploadFileDto uploadFileDto = uploadFileService.findUploadFileDto(storeFileName);
        UrlResource urlResource = new UrlResource("file:" + fileStore.getFullPath(uploadFileDto.getStoreFileName()));

        String encodedUploadedOriginalFileName = UriUtils.encode(uploadFileDto.getOriginalFileName(), StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedUploadedOriginalFileName + "\"";

        //content-type 찾기
        String contentType = Files.probeContentType(Paths.get(fileStore.getFullPath(uploadFileDto.getStoreFileName())));

        ResponseEntity.BodyBuilder builder = ResponseEntity.ok();

        //content-type 설정하기
        if (contentType != null) {
            builder.contentType(MediaType.parseMediaType(contentType));
        }

        return builder
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(urlResource);
    }
}
