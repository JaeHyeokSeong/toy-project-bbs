package hello.board.domain.repository.member.query.dto;

import hello.board.entity.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MemberDto {

    private Long memberId;
    private String name;
    private String email;
    private LocalDateTime joinedAt;
    private RoleType roleType;
}
