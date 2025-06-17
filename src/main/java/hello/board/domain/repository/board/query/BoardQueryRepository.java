package hello.board.domain.repository.board.query;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.board.domain.repository.board.query.dto.BoardSearchCondition;
import hello.board.domain.repository.board.query.dto.SearchSort;
import hello.board.domain.repository.board.query.dto.SearchTarget;
import hello.board.entity.Board;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static hello.board.entity.QBoard.board;
import static hello.board.entity.QMember.member;


@Repository
public class BoardQueryRepository {

    private final JPAQueryFactory queryFactory;

    public BoardQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Page<Board> findAllBoards(BoardSearchCondition condition, Pageable pageable) {
        List<Board> content = queryFactory
                .selectFrom(board)
                .join(board.member, member).fetchJoin()
                .where(search(condition))
                .orderBy(sort(condition.getSortBy()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(board.count())
                .from(board)
                .join(board.member, member)
                .where(search(condition))
                .fetchOne();

        return new PageImpl<>(content, pageable, count);
    }

    private BooleanExpression search(BoardSearchCondition condition) {
        String searchTerm = condition.getSearchTerm();
        SearchTarget searchTarget = condition.getSearchTarget();

        if (searchTarget == null || !StringUtils.hasText(searchTerm)) {
            return null;
        } else if (searchTarget == SearchTarget.TITLE) {
            return board.title.contains(searchTerm);
        } else if (searchTarget == SearchTarget.CONTENT) {
            return board.content.contains(searchTerm);
        } else {
            return member.name.eq(searchTerm);
        }
    }

    private OrderSpecifier<LocalDateTime> sort(SearchSort searchSort) {
        if (searchSort == null || searchSort == SearchSort.NEWEST) {
            return board.createdDate.desc();
        }
        return board.createdDate.asc();
    }
}
