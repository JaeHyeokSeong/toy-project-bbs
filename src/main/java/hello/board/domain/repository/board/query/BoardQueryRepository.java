package hello.board.domain.repository.board.query;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.board.domain.repository.board.query.dto.BoardQueryDto;
import hello.board.domain.repository.board.query.dto.BoardSearchCondition;
import hello.board.domain.repository.board.query.dto.SearchSort;
import hello.board.domain.repository.board.query.dto.SearchTarget;
import hello.board.domain.repository.upload_file.query.dto.UploadFileQueryDto;
import hello.board.entity.ReactionType;
import hello.board.entity.board.Board;
import hello.board.entity.file.QUploadFile;
import hello.board.entity.member.QMember;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static hello.board.entity.board.QBoard.board;
import static hello.board.entity.board.QBoardReaction.boardReaction;
import static hello.board.entity.file.QUploadFile.*;
import static hello.board.entity.member.QMember.member;

@Slf4j
@Transactional
@Repository
public class BoardQueryRepository {

    private final JPAQueryFactory queryFactory;

    public BoardQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Optional<BoardQueryDto> findBoardQueryDto(Long boardId, String slug, @Nullable Long memberId) {

        BoardQueryDto boardQueryDto;

        if (memberId == null) {
            boardQueryDto = queryFactory.select(Projections.fields(BoardQueryDto.class,
                            board.id.as("boardId"),
                            board.title.as("boardTitle"),
                            board.content.as("boardContent"),
                            board.views.as("boardViews"),
                            board.createdDate,
                            board.lastModifiedDate,
                            member.id.as("writerId"),
                            member.name.as("writerName"),
                            member.roleType.as("writerRoleType"),
                            Expressions.as(JPAExpressions.select(boardReaction.count())
                                    .from(boardReaction)
                                    .where(boardReaction.board.id.eq(boardId),
                                            boardReaction.reactionType.eq(ReactionType.LIKE)), "totalLikesCount"),
                            Expressions.as(JPAExpressions.select(boardReaction.count())
                                    .from(boardReaction)
                                    .where(boardReaction.board.id.eq(boardId),
                                            boardReaction.reactionType.eq(ReactionType.DISLIKE)), "totalDislikesCount")
                    ))
                    .from(board)
                    .join(board.member, member)
                    .where(board.id.eq(boardId), board.slug.eq(slug))
                    .fetchOne();
        } else {
            QMember subMember = new QMember("subMember");
            boardQueryDto = queryFactory.select(Projections.fields(BoardQueryDto.class,
                            board.id.as("boardId"),
                            board.title.as("boardTitle"),
                            board.content.as("boardContent"),
                            board.views.as("boardViews"),
                            board.createdDate,
                            board.lastModifiedDate,
                            member.id.as("writerId"),
                            member.name.as("writerName"),
                            member.roleType.as("writerRoleType"),
                            Expressions.as(JPAExpressions.select(boardReaction.count())
                                    .from(boardReaction)
                                    .where(boardReaction.board.id.eq(boardId),
                                            boardReaction.reactionType.eq(ReactionType.LIKE)),
                                    "totalLikesCount"),
                            Expressions.as(JPAExpressions.select(boardReaction.count())
                                    .from(boardReaction)
                                    .where(boardReaction.board.id.eq(boardId),
                                            boardReaction.reactionType.eq(ReactionType.DISLIKE)),
                                    "totalDislikesCount"),
                            Expressions.as(JPAExpressions.select(subMember.name)
                                    .from(subMember)
                                    .where(subMember.id.eq(memberId)), "viewerName"),
                            Expressions.as(JPAExpressions.select(subMember.roleType)
                                    .from(subMember)
                                    .where(subMember.id.eq(memberId)), "viewerRoleType"),
                            Expressions.as(JPAExpressions.select(boardReaction.reactionType)
                                    .from(boardReaction)
                                    .where(boardReaction.board.id.eq(boardId), boardReaction.member.id.eq(memberId)),
                                    "viewerReactionType")
                    ))
                    .from(board)
                    .join(board.member, member)
                    .where(board.id.eq(boardId), board.slug.eq(slug))
                    .fetchOne();
        }

        List<UploadFileQueryDto> uploadedFiles = queryFactory.select(Projections.constructor(UploadFileQueryDto.class,
                        uploadFile.id,
                        uploadFile.originalFileName,
                        uploadFile.storeFileName,
                        uploadFile.createdDate,
                        uploadFile.lastModifiedDate))
                .from(uploadFile)
                .where(uploadFile.board.id.eq(boardId))
                .fetch();

        //게시물에 있는 모든 파일들 조회
        if (boardQueryDto != null) {
            boardQueryDto.setUploadedFiles(uploadedFiles);
        }

        return Optional.ofNullable(boardQueryDto);
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
