package hello.board.entity.member;

import hello.board.entity.BaseEntity;
import hello.board.entity.RoleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    public Member(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        roleType = RoleType.USER;
    }
}
