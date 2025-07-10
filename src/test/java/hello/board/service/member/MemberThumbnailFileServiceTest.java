package hello.board.service.member;

import hello.board.entity.member.Member;
import hello.board.repository.member.query.MemberThumbnailFileQueryRepository;
import hello.board.repository.member.query.dto.MemberThumbnailFileQueryDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberThumbnailFileServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberThumbnailFileService memberThumbnailFileService;
    @Autowired
    MemberThumbnailFileQueryRepository memberThumbnailFileQueryRepository;

    @Test
    void 회원_썸네일사진_저장() {
        //given
        Member member = new Member("member1", "email1", "password1");
        memberService.join(member);

        String originalFileName = "ofn1";
        String storeFileName = "sfn1";

        //when
        Optional<String> deleteFileName = memberThumbnailFileService
                .changeMemberThumbnailFile(member.getId(), originalFileName, storeFileName);

        //then
        Optional<MemberThumbnailFileQueryDto> result = memberThumbnailFileQueryRepository
                .findMemberThumbnailFileByStoreFileName(storeFileName);
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getOriginalFileName()).isEqualTo(originalFileName);
        assertThat(result.get().getStoreFileName()).isEqualTo(storeFileName);
        assertThat(deleteFileName.isEmpty()).isTrue();
    }

    @Test
    void 회원_썸네일사진_변경() {
        //given
        Member member = new Member("member1", "email1", "password1");
        memberService.join(member);

        String originalFileName = "ofn1";
        String storeFileName = "sfn1";
        memberThumbnailFileService.changeMemberThumbnailFile(member.getId(), originalFileName, storeFileName);

        String newOriginalFileName = "new_ofn1";
        String newStoreFileName = "new_sfn1";

        //when
        Optional<String> deleteStoreFileName = memberThumbnailFileService
                .changeMemberThumbnailFile(member.getId(), newOriginalFileName, newStoreFileName);

        //then
        assertThat(deleteStoreFileName).isPresent();
        assertThat(deleteStoreFileName.get()).isEqualTo(storeFileName);
        assertThat(memberThumbnailFileQueryRepository
                .findMemberThumbnailFileByStoreFileName(storeFileName).isEmpty()).isTrue();
        Optional<MemberThumbnailFileQueryDto> result = memberThumbnailFileQueryRepository
                .findMemberThumbnailFileByStoreFileName(newStoreFileName);
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getOriginalFileName()).isEqualTo(newOriginalFileName);
        assertThat(result.get().getStoreFileName()).isEqualTo(newStoreFileName);
    }
}