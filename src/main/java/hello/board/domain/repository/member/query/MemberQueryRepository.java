package hello.board.domain.repository.member.query;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.board.domain.repository.member.query.dto.MemberDto;
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

    public Optional<MemberDto> findMember(Long memberId) {
        MemberDto memberDto = queryFactory
                .select(Projections.constructor(MemberDto.class,
                        member.id,
                        member.name,
                        member.email,
                        member.createdDate,
                        member.roleType))
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne();

        return Optional.ofNullable(memberDto);
    }
}
