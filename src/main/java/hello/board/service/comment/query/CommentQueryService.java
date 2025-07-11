package hello.board.service.comment.query;

import hello.board.dto.ResponseData;
import hello.board.repository.comment.query.CommentQueryRepository;
import hello.board.repository.comment.query.dto.CommentDto;
import hello.board.repository.comment.query.dto.CommentSearchDto;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentQueryService {

    private final CommentQueryRepository commentQueryRepository;

    public ResponseData findAllComments(Long boardId, @Nullable Long memberId, CommentSearchDto searchDto,
                                        Pageable pageable) {
        return commentQueryRepository.findAllComments(boardId, memberId, searchDto, pageable);
    }
}
