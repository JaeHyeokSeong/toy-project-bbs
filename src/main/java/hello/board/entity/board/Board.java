package hello.board.entity.board;

import hello.board.entity.BaseEntity;
import hello.board.entity.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String title;
    private String slug; //board 찾을때에 url 경로 값 ex) /bbs/{pk}/slug
    @Lob
    private String content;
    private long views;

    @OneToMany(mappedBy = "board")
    private List<UploadFile> uploadFiles = new ArrayList<>();

    @OneToMany(mappedBy = "board")
    private List<BoardReaction> boardReactions = new ArrayList<>();

    //==연관관계 메서드==//
    public void addUploadFile(UploadFile uploadFile) {
        uploadFiles.add(uploadFile);
        uploadFile.changeBoard(this);
    }

    //==생성 메서드==//
    public static Board createBoard(String title, String content, Member member, List<UploadFile> uploadFiles) {
        Board board = new Board();
        board.title = title;
        board.slug = Slug.generate(title);
        board.content = content;
        board.member = member;
        board.views = 0;
        for (UploadFile uploadFile : uploadFiles) {
            board.addUploadFile(uploadFile);
        }
        return board;
    }

    //==비즈니스 로직==//
    public void changeTitle(String title) {
        this.title = title;
        slug = Slug.generate(title);
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void increaseView(int count) {
        views += count;
    }
}
