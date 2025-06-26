package hello.board.domain.repository.comment_reaction;

import hello.board.entity.ReactionType;
import hello.board.entity.comment.Comment;
import hello.board.entity.comment.CommentReaction;
import hello.board.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {

    Optional<CommentReaction> findByCommentAndMember(Comment comment, Member member);

    @Query("select count(cr) from CommentReaction cr where cr.comment.id = :commentId and cr.reactionType = :reactionType")
    Long totalReactionCounts(@Param("commentId") Long commentId, @Param("reactionType") ReactionType reactionType);
}
