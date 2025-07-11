package hello.board.repository.member.query;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.board.repository.member.query.dto.MemberThumbnailFileQueryDto;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static hello.board.entity.member.QMemberThumbnailFile.memberThumbnailFile;

@Repository
public class MemberThumbnailFileQueryRepository {

    private final JPAQueryFactory queryFactory;

    public MemberThumbnailFileQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Optional<MemberThumbnailFileQueryDto> findMemberThumbnailFile(Long memberId, String storeFileName) {
        MemberThumbnailFileQueryDto data = queryFactory.select(Projections.fields(MemberThumbnailFileQueryDto.class,
                        memberThumbnailFile.id.as("memberThumbnailFileId"),
                        memberThumbnailFile.originalFileName,
                        memberThumbnailFile.storeFileName,
                        memberThumbnailFile.createdDate,
                        memberThumbnailFile.lastModifiedDate,
                        memberThumbnailFile.member.id
                ))
                .from(memberThumbnailFile)
                .where(memberThumbnailFile.member.id.eq(memberId), memberThumbnailFile.storeFileName.eq(storeFileName))
                .fetchOne();

        return Optional.ofNullable(data);
    }
}
