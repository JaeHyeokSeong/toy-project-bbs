package hello.board.domain.service.board_reaction;

import hello.board.domain.repository.board.BoardRepository;
import hello.board.domain.repository.board_reaction.BoardReactionRepository;
import hello.board.domain.repository.member.MemberRepository;
import hello.board.entity.Board;
import hello.board.entity.BoardReaction;
import hello.board.entity.Member;
import hello.board.entity.ReactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardReactionService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final BoardReactionRepository boardReactionRepository;

    /**
     * 게시물에 좋아요 또는 싫어요를 추가합니다.
     * 이때 처음으로 반응을 하는경우, 즉 해당 게시물에 대해서 처음으로 좋아요 또는 싫어요를 반응한 경우 해당 응답이 저장되어집니다.
     * 반면에 이미 해당게시물에 반응이 있는 경우
     * 1. 같은 반응이 온 경우, 이전 반응을 삭제 합니다. 예를 들어서 처음에 좋아요를 누른후, 다시 좋아요를 누른 경우 이 반응을 삭제
     * 2. 다른 반응이 온 경우, 이전 반응에서 새로운 반응으로 업데이트 합니다.
     *
     * @throws java.util.NoSuchElementException boardId 또는 memberId를 이용해서 Board 또는 Member을 못찾은 경우
     */
    public void reflectReaction(Long boardId, Long memberId, ReactionType reactionType) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        Member member = memberRepository.findById(memberId).orElseThrow();
        Optional<BoardReaction> findBoardReaction = boardReactionRepository.findByBoardAndMember(board, member);

        //사용자가 해당 게시물에 어떠한 반응도 안한경우
        if (findBoardReaction.isEmpty()) {
            BoardReaction boardReaction = new BoardReaction(member, board, reactionType);
            boardReactionRepository.save(boardReaction);
            return;
        }

        //사용자가 해당 게시물에 반응을 이미한경우
        BoardReaction boardReaction = findBoardReaction.get();

        //이전에 했던 반응에서 또다시 같은 반응을 한 경우, 즉 이전에 했던 반응을 취소하고 싶은 경우
        if (boardReaction.getReactionType() == reactionType) {
            boardReactionRepository.delete(boardReaction);
        } else {
            boardReaction.changeReactionType(reactionType);
        }
    }

    /**
     * boardId에 해당하는 게시물의 전체 (좋아요 수 - 싫어요 수) 결과를 반환 합니다.
     * @throws java.util.NoSuchElementException boardId를 이용해서 Board를 못찾은 경우
     */
    @Transactional(readOnly = true)
    public long totalReactionCount(Long boardId) {
        //존재하는 게시물인지 확인하기
        boardRepository.findById(boardId).orElseThrow();

        //해당 게시물의 전체 좋아요 수
        long totalLikeCount = boardReactionRepository
                .totalCountByBoardIdAndReactionType(boardId, ReactionType.LIKE);
        //해당 게시물의 전체 싫어요 수
        long totalDisLikeCount = boardReactionRepository
                .totalCountByBoardIdAndReactionType(boardId, ReactionType.DISLIKE);

        return totalLikeCount - totalDisLikeCount;
    }
}
