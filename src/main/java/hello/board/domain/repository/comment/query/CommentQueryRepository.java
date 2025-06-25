package hello.board.domain.repository.comment.query;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.board.domain.repository.comment.query.dto.CommentDto;
import hello.board.domain.repository.comment.query.dto.CommentSearchDto;
import hello.board.domain.repository.comment.query.dto.CommentSearchSort;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static hello.board.entity.QComment.comment;
import static hello.board.entity.QMember.member;

@Repository
public class CommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    public CommentQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Slice<CommentDto> findAllComments(Long boardId, @Nullable Long memberId,
                                             CommentSearchDto searchDto, Pageable pageable) {

        List<CommentDto> content = queryFactory.select(Projections.constructor(CommentDto.class,
                        comment.id,
                        comment.content,
                        comment.createdDate,
                        comment.lastModifiedDate,
                        comment.board.id,
                        member.name,
                        memberId == null ? Expressions.asBoolean(false) : member.id.eq(memberId)
                ))
                .from(comment)
                .where(comment.board.id.eq(boardId), parentCommentIdEq(searchDto.getParentCommentId()))
                .join(comment.member, member)
                .orderBy(commentSearchSortEq(searchDto.getSearchSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            hasNext = true;

            List<CommentDto> recreatedContent = new ArrayList<>();
            for (int i = 0; i < pageable.getPageSize(); i++) {
                recreatedContent.add(content.get(i));
            }
            return new SliceImpl<>(recreatedContent, pageable, hasNext);
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    public Long totalCount(Long boardId, @Nullable Long parentCommentId) {
        return queryFactory.select(comment.count())
                .from(comment)
                .where(comment.board.id.eq(boardId), parentCommentId == null ? comment.parentComment.isNull() :
                        comment.parentComment.id.eq(parentCommentId))
                .fetchOne();
    }

    private BooleanExpression parentCommentIdEq(Long parentCommentId) {
        return parentCommentId == null ? comment.parentComment.isNull() : comment.parentComment.id.eq(parentCommentId);
    }

    private OrderSpecifier<?> commentSearchSortEq(CommentSearchSort searchSort) {
        if (searchSort == CommentSearchSort.NEWEST) {
            return comment.createdDate.desc();
        } else {
            return comment.createdDate.asc();
        }
    }
}
