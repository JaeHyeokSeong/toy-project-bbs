package hello.board.entity.board;

import hello.board.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UploadFile extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "upload_file_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    private String originalFileName; //원본 파일 이름
    private String storeFileName; //서버에 저장되어질 파일 이름

    public UploadFile(String originalFileName, String storeFileName) {
        this.originalFileName = originalFileName;
        this.storeFileName = storeFileName;
    }

    //==연관관계 메서드==//
    public void changeBoard(Board board) {
        this.board = board;
    }
}
