package hello.board.domain.service.member.query;

import hello.board.domain.repository.member.query.MemberQueryRepository;
import hello.board.domain.repository.member.query.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService {

    private final MemberQueryRepository memberQueryRepository;

    public Optional<MemberDto> findMember(Long memberId) {
        return memberQueryRepository.findMember(memberId);
    }
}
