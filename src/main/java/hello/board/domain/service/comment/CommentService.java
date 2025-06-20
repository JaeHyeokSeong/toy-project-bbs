package hello.board.domain.service.comment;

import hello.board.domain.repository.comment.CommentRepository;
import hello.board.domain.service.board.BoardService;
import hello.board.domain.service.member.MemberService;
import hello.board.entity.Board;
import hello.board.entity.Comment;
import hello.board.entity.Member;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final BoardService boardService;
    private final MemberService memberService;
    private final CommentRepository commentRepository;

    public Comment addComment(Long boardId, Long memberId, String content, @Nullable Long parentCommentId) {
        Board board = boardService.findById(boardId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 boardId 입니다."));
        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 memberId 입니다."));

        Comment parentComment = null;
        //대댓글 인경우
        if (parentCommentId != null) {
            parentComment = commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 commentId 입니다."));
        }

        Comment comment = Comment.createComment(board, member, content, parentComment);
        return commentRepository.save(comment);
    }
}
