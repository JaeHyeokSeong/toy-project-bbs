package hello.board.api.member.dto;

import hello.board.domain.repository.member.query.dto.MemberQueryDto;
import lombok.Data;

@Data
public class MemberProfileDto {

    private String message;
    private MemberQueryDto data;

    public MemberProfileDto(String message, MemberQueryDto data) {
        this.message = message;
        this.data = data;
    }
}
