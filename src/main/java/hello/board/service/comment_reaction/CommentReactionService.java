package hello.board.service.comment_reaction;

import hello.board.repository.comment.CommentRepository;
import hello.board.repository.comment_reaction.CommentReactionRepository;
import hello.board.service.member.MemberService;
import hello.board.entity.ReactionType;
import hello.board.entity.comment.Comment;
import hello.board.entity.comment.CommentReaction;
import hello.board.entity.member.Member;
import hello.board.exception.CommentNotFoundException;
import hello.board.exception.MemberNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentReactionService {

    private final MemberService memberService;
    private final CommentRepository commentRepository;
    private final CommentReactionRepository commentReactionRepository;

    public void reflectReaction(@NotNull Long commentId, @NotNull Long memberId, @NotNull ReactionType reactionType) {

        //존재하는 comment인지 확인하기
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new CommentNotFoundException("존재하지 않는 comment입니다. 전달된 commentId=" + commentId)
        );

        //존재하는 회원인지 확인하기
        Member member = memberService.findById(memberId).orElseThrow(
                () -> new MemberNotFoundException("존재하지 않는 회원입니다. 전달된 memberId=" + memberId)
        );

        //전달된 commentId 그리고 memberId을 이용해서 어떠한 commentReaction을 남겼는지 확인하기
        Optional<CommentReaction> findCommentReaction = commentReactionRepository
                .findByCommentAndMember(comment, member);

        //만약에 현재 memberId가 commentId에 대해서 어떠한 반응도 안남긴 경우
        if (findCommentReaction.isEmpty()) {
            commentReactionRepository.save(new CommentReaction(comment, member, reactionType));
            return;
        }

        //만약에 현재 memberId가 commentId에 대해서 어떠한 반응을 이미 남긴 경우
        CommentReaction commentReaction = findCommentReaction.get();

        //좋아요 반응을 이미 남겼는데, 다시 좋아요 반응이 온경우
        //싫어요 반응을 이미 남겼는데, 다시 싫어요 반응이 온경우
        //이전에 남긴 반응을 취소하겠다는 의미
        if (commentReaction.getReactionType() == reactionType) {
            commentReactionRepository.deleteById(commentReaction.getId());
        } else {
            //반응을 변경하겠다는 의미
            //좋아요 -> 싫어요 또는 싫어요 -> 좋아요
            commentReaction.changeReactionType(reactionType);
        }
    }

    @Transactional(readOnly = true)
    public long totalReactionCount(Long commentId) {
        commentRepository.findById(commentId).orElseThrow(
                () -> new CommentNotFoundException("존재하지 않는 comment입니다. 전달된 commentId=" + commentId));

        //전체 좋아요 수
        Long totalLikes = commentReactionRepository.totalReactionCounts(commentId, ReactionType.LIKE);
        //전체 싫어요 수
        Long totalDislikes = commentReactionRepository.totalReactionCounts(commentId, ReactionType.DISLIKE);

        return totalLikes - totalDislikes;
    }
}
