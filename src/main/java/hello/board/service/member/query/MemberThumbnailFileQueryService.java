package hello.board.service.member.query;

import hello.board.exception.MemberThumbnailFileNotFoundException;
import hello.board.repository.member.query.MemberThumbnailFileQueryRepository;
import hello.board.repository.member.query.dto.MemberThumbnailFileQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberThumbnailFileQueryService {

    private final MemberThumbnailFileQueryRepository memberThumbnailFileQueryRepository;

    public MemberThumbnailFileQueryDto findMemberThumbnailFile(Long memberId, String storeFileName) {
        return memberThumbnailFileQueryRepository.findMemberThumbnailFile(memberId, storeFileName)
                .orElseThrow(() -> new MemberThumbnailFileNotFoundException(
                        "회원의 thumbnail 파일을 찾지 못했습니다. " +
                                "전달된 memberId=" + memberId + ", storeFileName=" + storeFileName)
                );
    }
}
