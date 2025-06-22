package hello.board.domain.repository.board_reaction;

import hello.board.entity.Board;
import hello.board.entity.BoardReaction;
import hello.board.entity.Member;
import hello.board.entity.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardReactionRepository extends JpaRepository<BoardReaction, Long> {

    Optional<BoardReaction> findByBoardAndMember(Board board, Member member);

    @Query("select count(br) from BoardReaction br where br.board.id = :boardId and br.reactionType = :reactionType")
    long totalCountByBoardIdAndReactionType(@Param("boardId") Long boardId,
                                            @Param("reactionType") ReactionType reactionType);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from BoardReaction br where br.board.id = :boardId")
    void deleteAllBoardReaction(@Param("boardId") Long boardId);
}
