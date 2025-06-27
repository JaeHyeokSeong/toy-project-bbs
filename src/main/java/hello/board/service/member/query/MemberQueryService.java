package hello.board.service.member.query;

import hello.board.repository.member.query.MemberQueryRepository;
import hello.board.repository.member.query.dto.MemberQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService {

    private final MemberQueryRepository memberQueryRepository;

    public Optional<MemberQueryDto> findMember(Long memberId) {
        return memberQueryRepository.findMember(memberId);
    }
}
