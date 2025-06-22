package hello.board.domain.repository.comment.query;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.board.domain.repository.comment.query.dto.CommentParentDto;
import hello.board.entity.QComment;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static hello.board.entity.QComment.*;

@Repository
public class CommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    public CommentQueryRepository(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public Slice<CommentParentDto> findAllCommentsParent(Long boardId, Pageable pageable) {
        List<CommentParentDto> content = queryFactory.select(Projections.constructor(CommentParentDto.class,
                        comment.id,
                        comment.board.id,
                        comment.member.id,
                        comment.createdDate,
                        comment.content
                ))
                .from(comment)
                .where(comment.board.id.eq(boardId))
                .orderBy(comment.createdDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            hasNext = true;

            List<CommentParentDto> recreatedContent = new ArrayList<>();
            for (int i = 0; i < pageable.getPageSize(); i++) {
                recreatedContent.add(content.get(i));
            }
            return new SliceImpl<>(recreatedContent, pageable, hasNext);
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }
}
