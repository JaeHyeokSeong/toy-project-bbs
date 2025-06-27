package hello.board.repository.member.query;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.board.repository.member.query.dto.MemberQueryDto;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static hello.board.entity.member.QMember.member;

@Repository
public class MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    public MemberQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Optional<MemberQueryDto> findMember(Long memberId) {
        MemberQueryDto memberQueryDto = queryFactory
                .select(Projections.constructor(MemberQueryDto.class,
                        member.id,
                        member.name,
                        member.email,
                        member.createdDate,
                        member.roleType))
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne();

        return Optional.ofNullable(memberQueryDto);
    }
}
