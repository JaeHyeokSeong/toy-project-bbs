package hello.board.controller.api.member;

import hello.board.SessionConst;
import hello.board.dto.ResponseResult;
import hello.board.entity.FileStore;
import hello.board.repository.member.query.dto.MemberQueryDto;
import hello.board.repository.member.query.dto.MemberThumbnailFileQueryDto;
import hello.board.service.member.query.MemberQueryService;
import hello.board.service.member.query.MemberThumbnailFileQueryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.Optional;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberQueryService memberQueryService;
    private final FileStore fileStore;
    private final MemberThumbnailFileQueryService memberThumbnailFileQueryService;

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
}
