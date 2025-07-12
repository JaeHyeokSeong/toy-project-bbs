package hello.board.controller.api.member;

import hello.board.SessionConst;
import hello.board.controller.api.member.dto.ChangeMemberThumbnailFileDto;
import hello.board.dto.ResponseResult;
import hello.board.entity.FileStore;
import hello.board.exception.BindingResultException;
import hello.board.exception.EmptyFileException;
import hello.board.repository.member.query.dto.MemberQueryDto;
import hello.board.repository.member.query.dto.MemberThumbnailFileQueryDto;
import hello.board.service.member.MemberThumbnailFileService;
import hello.board.service.member.query.MemberQueryService;
import hello.board.service.member.query.MemberThumbnailFileQueryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberQueryService memberQueryService;
    private final FileStore fileStore;
    private final MemberThumbnailFileQueryService memberThumbnailFileQueryService;
    private final MemberThumbnailFileService memberThumbnailFileService;

    @GetMapping("/profile")
    public ResponseResult profile(@SessionAttribute(value = SessionConst.MEMBER_ID, required = false) Long memberId,
                                  HttpServletRequest request) {

        //비로그인 회원 접근
        if (memberId == null) {
            return new ResponseResult(HttpStatus.OK.toString(), "비로그인 회원", null);
        }

        Optional<MemberQueryDto> findMember = memberQueryService.findMember(memberId);

        if (findMember.isEmpty()) {
            HttpSession session = request.getSession(false);
            session.invalidate();
            return new ResponseResult(HttpStatus.OK.toString(), "비로그인 회원", null);
        }

        //로그인 회원 접근
        return new ResponseResult(HttpStatus.OK.toString(), "로그인 회원", findMember.get());
    }

    @GetMapping("/thumbnails/{memberId}/{storeFileName}")
    public ResponseEntity<Resource> thumbnail(@PathVariable Long memberId, @PathVariable String storeFileName) throws IOException {

        MemberThumbnailFileQueryDto memberThumbnailFileDto = memberThumbnailFileQueryService
                .findMemberThumbnailFile(memberId, storeFileName);

        String fullPath = fileStore.getFullPath(memberThumbnailFileDto.getStoreFileName());

        UrlResource resource = new UrlResource("file:" + fullPath);
        String contentType = Files.probeContentType(Paths.get(fileStore.getFullPath(fullPath)));

        ResponseEntity.BodyBuilder builder = ResponseEntity.ok();

        //content-type 헤더 설정
        if (contentType != null) {
            builder.contentType(MediaType.valueOf(contentType));
        }

        return builder
                .cacheControl(CacheControl.noCache())
                .lastModified(memberThumbnailFileDto.getLastModifiedDate().atZone(ZoneId.of("Asia/Seoul")))
                .body(resource);
    }

    @PostMapping("/thumbnail")
    public ResponseResult changeThumbnail(@SessionAttribute(value = SessionConst.MEMBER_ID) Long memberId,
                                          @Valid @ModelAttribute ChangeMemberThumbnailFileDto dto,
                                          BindingResult bindingResult) {

        //검증 오류
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            throw new BindingResultException(bindingResult.getAllErrors());
        }

        //파일 정보
        String storeFileName = fileStore.storeFile(dto.getMultipartFile())
                .orElseThrow(() -> new EmptyFileException("파일저장 실패, 전달된 파일이 없습니다."));
        String originalFilename = dto.getMultipartFile().getOriginalFilename();

        //회원 thumbnail 파일 변경
        Optional<String> deleteStoreFileName = memberThumbnailFileService
                .changeMemberThumbnailFile(memberId, originalFilename, storeFileName);

        //이전에 존재하던 thumbnail 파일을 삭제
        deleteStoreFileName.ifPresent(fileStore::deleteFile);

        return new ResponseResult(
                HttpStatus.OK.toString(),
                "회원 thumbnail 사진 변경 완료.",
                new MemberThumbnailFileDto(originalFilename, storeFileName)
        );
    }

    @Getter
    private static class MemberThumbnailFileDto {

        private final String originalFileName;
        private final String storeFileName;

        public MemberThumbnailFileDto(String originalFileName, String storeFileName) {
            this.originalFileName = originalFileName;
            this.storeFileName = storeFileName;
        }
    }
}
