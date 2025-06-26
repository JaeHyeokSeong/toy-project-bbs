package hello.board.entity.board;

import hello.board.entity.BaseEntity;
import hello.board.entity.member.Member;
import hello.board.entity.ReactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardReaction extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "board_reaction_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType reactionType;

    public BoardReaction(Member member, Board board, ReactionType reactionType) {
        this.member = member;
        this.board = board;
        this.reactionType = reactionType;
    }

    //==비즈니스 로직==//
    public void changeReactionType(ReactionType reactionType) {
        this.reactionType = reactionType;
    }
}
