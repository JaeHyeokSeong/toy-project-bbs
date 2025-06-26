package hello.board.domain.service.upload_file;

import hello.board.domain.repository.upload_file.UploadFileRepository;
import hello.board.domain.service.upload_file.dto.UploadFileDto;
import hello.board.entity.file.UploadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class UploadFileService {

    private final UploadFileRepository uploadFileRepository;

    /**
     * @throws java.util.NoSuchElementException storeFileName으로 못찾은 경우
     */
    public UploadFileDto findUploadFileDto(String storeFileName) {
        UploadFile uploadFile = uploadFileRepository.findByStoreFileName(storeFileName)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 파일입니다. 전달된 파일명: " + storeFileName));
        return new UploadFileDto(uploadFile);
    }
}
