package hello.board.service.comment;

import hello.board.entity.board.Board;
import hello.board.entity.comment.Comment;
import hello.board.entity.member.Member;
import hello.board.exception.BoardNotFoundException;
import hello.board.exception.CommentNotFoundException;
import hello.board.exception.MemberNotFoundException;
import hello.board.repository.comment.CommentRepository;
import hello.board.repository.comment_reaction.CommentReactionRepository;
import hello.board.service.board.BoardService;
import hello.board.service.comment.dto.UpdateCommentResultDto;
import hello.board.service.member.MemberService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final BoardService boardService;
    private final MemberService memberService;
    private final CommentRepository commentRepository;
    private final CommentReactionRepository commentReactionRepository;

    public Comment addComment(Long boardId, Long memberId, String content, @Nullable Long parentCommentId) {
        Board board = boardService.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("존재하지 않는 boardId 입니다."));
        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 memberId 입니다."));

        Comment parentComment = null;
        //대댓글 인경우
        if (parentCommentId != null) {
            parentComment = commentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new CommentNotFoundException("존재하지 않는 commentId 입니다."));
        }

        Comment comment = Comment.createComment(board, member, content, parentComment);
        return commentRepository.save(comment);
    }

    /**
     * comment 내용 수정
     *
     * @throws CommentNotFoundException 접근권환이 없는 경우
     */
    public UpdateCommentResultDto changeContent(Long commentId, Long memberId, String content) {
        //권환 확인
        Comment comment = commentRepository.findComment(commentId, memberId)
                .orElseThrow(() -> new CommentNotFoundException(
                        "commentId: " + commentId +
                                " 에 대해 접근 권환이 없습니다.")
                );

        comment.changeContent(content);
        return new UpdateCommentResultDto(comment);
    }

    /**
     * comment 삭제
     *
     * @throws CommentNotFoundException 접근권환이 없는 경우
     */
    public void deleteComment(Long commentId, Long memberId) {
        //권환 확인
        commentRepository.findComment(commentId, memberId)
                .orElseThrow(() -> new CommentNotFoundException(
                        "commentId: " + commentId + " 에 대해 접근 권환이 없습니다.")
                );

        //현재 답변에 달린 모든 reaction들 지우기
        commentReactionRepository.deleteAllParentCommentReactionsByCommentId(commentId);
        commentReactionRepository.deleteAllChildCommentReactionsByCommentId(commentId);

        //현재 답변에 달린 모둔 답글들 지우기
        commentRepository.deleteAllChildComments(commentId);
        commentRepository.deleteByCommentId(commentId);
    }
}
