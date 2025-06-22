package hello.board.domain.service.comment.query;

import hello.board.domain.repository.comment.query.CommentQueryRepository;
import hello.board.domain.repository.comment.query.dto.CommentParentDto;
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

    public Slice<CommentParentDto> findAllCommentsParent(Long boardId, Pageable pageable) {
        return commentQueryRepository.findAllCommentsParent(boardId, pageable);
    }
}
