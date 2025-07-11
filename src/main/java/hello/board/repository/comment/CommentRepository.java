package hello.board.repository.comment;

import hello.board.entity.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Comment c where c.board.id = :boardId and c.parentComment is null")
    void deleteAllParentCommentsByBoardId(@Param("boardId") Long boardId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Comment c where c.board.id = :boardId and c.parentComment is not null")
    void deleteAllChildCommentsByBoardId(@Param("boardId") Long boardId);

    @Query("select c from Comment c where c.id = :commentId and c.member.id = :memberId")
    Optional<Comment> findComment(@Param("commentId") Long commentId, @Param("memberId") Long memberId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Comment c where c.id = :commentId")
    void deleteByCommentId(@Param("commentId") Long commentId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Comment c where c.parentComment.id = :commentId")
    void deleteAllChildComments(@Param("commentId") Long commentId);
}
