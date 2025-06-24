package hello.board.api.upload_file;

import hello.board.domain.service.upload_file.UploadFileService;
import hello.board.domain.service.upload_file.dto.UploadFileDto;
import hello.board.entity.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileApiController {

    private final UploadFileService uploadFileService;
    private final FileStore fileStore;

    @GetMapping("/images/{storeFileName}")
    public ResponseEntity<Resource> image(@PathVariable String storeFileName) throws MalformedURLException {
        UploadFileDto uploadFileDto = uploadFileService.findUploadFileDto(storeFileName);
        UrlResource content = new UrlResource("file:" + fileStore.getFullPath(uploadFileDto.getStoreFileName()));

        CacheControl cacheControl = CacheControl.noCache();

        return ResponseEntity
                .ok()
                .cacheControl(cacheControl)
                .lastModified(uploadFileDto.getLastModifiedDate().atZone(ZoneId.of("Asia/Seoul")))
                .body(content);
    }

    @GetMapping("/files/{storeFileName}")
    public ResponseEntity<Resource> file(@PathVariable String storeFileName) throws MalformedURLException {
        UploadFileDto uploadFileDto = uploadFileService.findUploadFileDto(storeFileName);
        UrlResource urlResource = new UrlResource("file:" + fileStore.getFullPath(uploadFileDto.getStoreFileName()));

        String encodedUploadedOriginalFileName = UriUtils.encode(uploadFileDto.getOriginalFileName(), StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedUploadedOriginalFileName + "\"";
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition).body(urlResource);
    }
}
