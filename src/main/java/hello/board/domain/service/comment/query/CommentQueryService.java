package hello.board.domain.service.comment.query;

import hello.board.domain.repository.comment.query.CommentQueryRepository;
import hello.board.domain.repository.comment.query.dto.CommentDto;
import hello.board.domain.repository.comment.query.dto.CommentSearchDto;
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

    public Slice<CommentDto> findAllComments(Long boardId, @Nullable Long memberId, CommentSearchDto searchDto,
                                             Pageable pageable) {
        return commentQueryRepository.findAllComments(boardId, memberId, searchDto, pageable);
    }

    public Long totalCount(Long boardId, @Nullable Long parentCommentId) {
        return commentQueryRepository.totalCount(boardId, parentCommentId);
    }
}
