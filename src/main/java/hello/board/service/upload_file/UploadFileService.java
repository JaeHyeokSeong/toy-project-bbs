package hello.board.service.upload_file;

import hello.board.entity.file.UploadFile;
import hello.board.exception.UploadFileNotFound;
import hello.board.repository.upload_file.UploadFileRepository;
import hello.board.repository.upload_file.dto.UploadFileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class UploadFileService {

    private final UploadFileRepository uploadFileRepository;

    public UploadFileDto saveUploadFile(UploadFile uploadFile) {
        uploadFileRepository.save(uploadFile);
        return new UploadFileDto(uploadFile);
    }

    /**
     * @throws UploadFileNotFound storeFileName으로 못찾은 경우
     */
    public UploadFileDto findUploadFileDto(String storeFileName) {
        UploadFile uploadFile = uploadFileRepository.findByStoreFileName(storeFileName)
                .orElseThrow(() -> new UploadFileNotFound("존재하지 않는 파일입니다. 전달된 파일명: " + storeFileName));
        return new UploadFileDto(uploadFile);
    }
}
