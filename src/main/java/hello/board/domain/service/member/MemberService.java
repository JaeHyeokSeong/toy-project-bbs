package hello.board.domain.service.member;

import hello.board.domain.repository.member.MemberRepository;
import hello.board.entity.member.Member;
import hello.board.exception.DuplicateEmailException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void join(Member member) {
        Optional<Member> findMember = memberRepository.findByEmail(member.getEmail());
        if (findMember.isPresent()) {
            throw new DuplicateEmailException("이미 사용중인 이메일 입니다.");
        }
        memberRepository.save(member);
    }

    public Optional<Member> findById(Long memberId) {
        return memberRepository.findById(memberId);
    }

    public Optional<Long> login(String email, String password) {
        Optional<Member> findMember = memberRepository.findByEmailAndPassword(email, password);
        if (findMember.isPresent()) {
            Long memberId = findMember.get().getId();
            return Optional.of(memberId);
        }
        return Optional.empty();
    }
}
