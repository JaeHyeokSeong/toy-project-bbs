package hello.board.api.member.dto;

import hello.board.domain.repository.member.query.dto.MemberDto;
import lombok.Data;

@Data
public class MemberProfileDto {

    private String message;
    private MemberDto data;

    public MemberProfileDto(String message, MemberDto data) {
        this.message = message;
        this.data = data;
    }
}
