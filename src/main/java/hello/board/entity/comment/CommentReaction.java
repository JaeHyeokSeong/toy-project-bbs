package hello.board.entity.comment;

import hello.board.entity.BaseEntity;
import hello.board.entity.ReactionType;
import hello.board.entity.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentReaction extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "comment_reaction_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private ReactionType reactionType;

    public CommentReaction(Comment comment, Member member, ReactionType reactionType) {
        this.comment = comment;
        this.member = member;
        this.reactionType = reactionType;
    }

    //==비즈니스 로직==//
    public void changeReactionType(ReactionType reactionType) {
        this.reactionType = reactionType;
    }
}
