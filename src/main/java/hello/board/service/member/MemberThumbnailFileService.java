package hello.board.service.member;

import hello.board.entity.member.Member;
import hello.board.entity.member.MemberThumbnailFile;
import hello.board.exception.MemberNotFoundException;
import hello.board.repository.member.MemberThumbnailFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberThumbnailFileService {

    private final MemberService memberService;
    private final MemberThumbnailFileRepository memberThumbnailFileRepository;

    /**
     * 회원의 thumbnail 파일을 변경
     *
     * @param memberId
     * @param originalFileName
     * @param storeFileName
     * @return 삭제해야할 storeFileName을 리턴함, 만약에 삭제할 파일이 없을 경우에는 Optional.empty()를 반환함
     * @throws MemberNotFoundException 전달된 memberId로 회원을 찾지 못한 경우
     */
    public Optional<String> changeMemberThumbnailFile(Long memberId, String originalFileName, String storeFileName) {

        Member member = memberService.findById(memberId).orElseThrow(
                () -> new MemberNotFoundException("존재하지 않는 회원입니다. 전달된 memberId=" + memberId));

        Optional<MemberThumbnailFile> findMemberThumbnailFile = memberThumbnailFileRepository.findByMemberId(memberId);

        if (findMemberThumbnailFile.isEmpty()) {
            //썸네일 파일 저장하기
            MemberThumbnailFile memberThumbnailFile = new MemberThumbnailFile(originalFileName, storeFileName, member);
            memberThumbnailFileRepository.save(memberThumbnailFile);
            return Optional.empty();
        }

        //썸네일 파일 수정하기
        MemberThumbnailFile memberThumbnailFile = findMemberThumbnailFile.get();

        //이전 파일 storeFileName
        String previousStoreFileName = memberThumbnailFile.getStoreFileName();

        //원본 파일이름 수정
        memberThumbnailFile.changeOriginalFileName(originalFileName);
        //서버에 저장된 파일이름 수정
        memberThumbnailFile.changeStoreFileName(storeFileName);

        return Optional.of(previousStoreFileName);
    }
}
