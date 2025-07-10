package hello.board.repository.member;

import hello.board.entity.member.MemberThumbnailFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberThumbnailFileRepository extends JpaRepository<MemberThumbnailFile, Long> {

    Optional<MemberThumbnailFile> findByMemberId(Long memberId);
}
