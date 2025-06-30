package hello.board.repository.board;

import hello.board.entity.board.Board;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Optional<Board> findByIdAndSlug(Long id, String slug);

    @Query("select b from Board b join fetch b.member m where b.id = :boardId and m.id = :memberId")
    Optional<Board> findBoard(@Param("boardId") Long boardId, @Param("memberId") Long memberId);
}
