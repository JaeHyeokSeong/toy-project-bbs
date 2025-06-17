package hello.board.domain.repository.comment;

import hello.board.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Comment c where c.board.id = :boardId")
    void deleteAllByBoardId(@Param("boardId") Long boardId);
}
