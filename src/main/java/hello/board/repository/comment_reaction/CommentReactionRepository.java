package hello.board.repository.comment_reaction;

import hello.board.entity.ReactionType;
import hello.board.entity.comment.Comment;
import hello.board.entity.comment.CommentReaction;
import hello.board.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {

    Optional<CommentReaction> findByCommentAndMember(Comment comment, Member member);

    @Query("select count(cr) from CommentReaction cr where cr.comment.id = :commentId and cr.reactionType = :reactionType")
    Long totalReactionCounts(@Param("commentId") Long commentId, @Param("reactionType") ReactionType reactionType);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from CommentReaction cr where cr.comment.id = :commentId")
    void deleteAllParentCommentReactionsByCommentId(@Param("commentId") Long commentId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from CommentReaction cr where cr.comment.id in" +
            " (select c.id from Comment c join c.parentComment pc where pc.id = :parentCommentId)")
    void deleteAllChildCommentReactionsByCommentId(Long parentCommentId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from CommentReaction cm where cm.comment.id in" +
            " (select c.id from Comment c join c.board b where b.id = :boardId)")
    void deleteAllByBoardId(@Param("boardId") Long boardId);
}
