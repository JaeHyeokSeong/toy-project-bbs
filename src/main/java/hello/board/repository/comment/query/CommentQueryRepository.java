package hello.board.repository.comment.query;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.board.entity.ReactionType;
import hello.board.entity.comment.QCommentReaction;
import hello.board.repository.comment.query.dto.CommentDto;
import hello.board.repository.comment.query.dto.CommentSearchDto;
import hello.board.repository.comment.query.dto.CommentSearchSort;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static hello.board.entity.comment.QComment.comment;
import static hello.board.entity.comment.QCommentReaction.commentReaction;
import static hello.board.entity.member.QMember.member;


@Repository
public class CommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    public CommentQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Page<CommentDto> findAllComments(Long boardId, @Nullable Long memberId,
                                            CommentSearchDto searchDto, Pageable pageable) {

        StringPath totalLikesPlusTotalDislikes = Expressions.stringPath("totalLikesPlusTotalDislikes");
        QCommentReaction subCommentReaction = new QCommentReaction("subCommentReaction");

        List<CommentDto> content = queryFactory.select(Projections.constructor(CommentDto.class,
                        comment.board.id,
                        comment.id,
                        comment.parentComment.id,
                        comment.content,
                        comment.createdDate,
                        comment.lastModifiedDate,
                        memberId == null ? Expressions.asBoolean(false) : member.id.eq(memberId),
                        Expressions.as(JPAExpressions.select(
                                        commentReaction.count().subtract(
                                                JPAExpressions.select(
                                                                subCommentReaction.count())
                                                        .from(subCommentReaction)
                                                        .where(subCommentReaction.comment.id.eq(comment.id),
                                                                subCommentReaction.reactionType.eq(ReactionType.DISLIKE))
                                        )
                                )
                                .from(commentReaction)
                                .where(commentReaction.comment.id.eq(comment.id),
                                        commentReaction.reactionType.eq(ReactionType.LIKE)), "totalLikesPlusTotalDislikes"),
                        memberId == null ? Expressions.constantAs(null, commentReaction.reactionType) :
                                JPAExpressions.select(commentReaction.reactionType)
                                        .from(commentReaction)
                                        .where(commentReaction.comment.id.eq(comment.id),
                                                commentReaction.member.id.eq(memberId)),
                        member.name,
                        member.roleType
                ))
                .from(comment)
                .where(comment.board.id.eq(boardId), parentCommentIdEq(searchDto.getParentCommentId()))
                .join(comment.member, member)
                .orderBy(commentSearchSortEq(searchDto.getSearchSort(), totalLikesPlusTotalDislikes), comment.createdDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        Long count = queryFactory.select(comment.count())
                .from(comment)
                .where(comment.board.id.eq(boardId), parentCommentIdEq(searchDto.getParentCommentId()))
                .fetchOne();

        if (content.size() > pageable.getPageSize()) {
            List<CommentDto> recreatedContent = new ArrayList<>();
            for (int i = 0; i < pageable.getPageSize(); i++) {
                recreatedContent.add(content.get(i));
            }
            return new PageImpl<>(recreatedContent, pageable, count);
        }

        return new PageImpl<>(content, pageable, count);
    }

    private BooleanExpression parentCommentIdEq(Long parentCommentId) {
        return parentCommentId == null ? comment.parentComment.isNull() : comment.parentComment.id.eq(parentCommentId);
    }

    private OrderSpecifier<?> commentSearchSortEq(CommentSearchSort searchSort, StringPath totalLikesPlusTotalDislikes) {
        if (searchSort == CommentSearchSort.NEWEST) {
            return comment.createdDate.desc();
        } else if (searchSort == CommentSearchSort.LIKES) {
            return totalLikesPlusTotalDislikes.desc();
        } else {
            return comment.createdDate.asc();
        }
    }
}
