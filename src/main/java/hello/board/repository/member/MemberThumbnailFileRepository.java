package hello.board.repository.member;

import hello.board.entity.member.MemberThumbnailFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberThumbnailFileRepository extends JpaRepository<MemberThumbnailFile, Long> {

    @Query("select mtf from MemberThumbnailFile mtf where mtf.member.id = :memberId")
    Optional<MemberThumbnailFile> findByMemberId(@Param("memberId") Long memberId);
}
