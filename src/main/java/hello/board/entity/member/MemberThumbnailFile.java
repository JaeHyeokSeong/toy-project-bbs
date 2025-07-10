package hello.board.entity.member;

import hello.board.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberThumbnailFile extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "member_thumbnail_file_id")
    private Long id;
    private String originalFileName;
    private String storeFileName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public MemberThumbnailFile(String originalFileName, String storeFileName, Member member) {
        this.originalFileName = originalFileName;
        this.storeFileName = storeFileName;
        this.member = member;
    }

    //==비즈니스 로직==//
    public void changeOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public void changeStoreFileName(String storeFileName) {
        this.storeFileName = storeFileName;
    }
}
