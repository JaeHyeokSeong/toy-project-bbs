package hello.board.domain.service.comment.query;

import hello.board.domain.repository.comment.query.CommentQueryRepository;
import hello.board.domain.repository.comment.query.dto.CommentDto;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentQueryService {

    private final CommentQueryRepository commentQueryRepository;

    public Slice<CommentDto> findAllComments(Long boardId, @Nullable Long parentCommentId,
                                             @Nullable Long memberId, Pageable pageable) {
        return commentQueryRepository.findAllComments(boardId, parentCommentId, memberId, pageable);
    }

    public Long totalCount(Long boardId, @Nullable Long parentCommentId) {
        return commentQueryRepository.totalCount(boardId, parentCommentId);
    }
}
