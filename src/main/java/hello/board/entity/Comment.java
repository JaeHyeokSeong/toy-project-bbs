package hello.board.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment")
    private List<Comment> childComments = new ArrayList<>();

    @Lob
    private String content;

    //==연관관계 메서드==//
    public void addChildComment(Comment comment) {
        childComments.add(comment);
        comment.setParentComment(this);
    }

    //==생성 메서드==//
    public static Comment createComment(Board board, Member member, String content, Comment parentComment) {
        Comment comment = new Comment();
        comment.board = board;
        comment.member = member;
        comment.content = content;
        if (parentComment != null) {
            parentComment.addChildComment(comment);
        }
        return comment;
    }

    //==비즈니스 로직==//
    public void changeContent(String content) {
        this.content = content;
    }
}
