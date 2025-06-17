package hello.board.domain.service.member;

import hello.board.entity.Member;
import hello.board.exception.DuplicateEmailException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Test
    void 회원가입_성공() {
        //given
        Member member = new Member("member1", "email1", "password1");

        //when
        memberService.join(member);

        //then
        Member findMember = memberService.findById(member.getId()).get();
        assertThat(findMember).isSameAs(member);
        assertThat(findMember.getId()).isNotNull();
        assertThat(findMember.getCreatedDate()).isNotNull();
        assertThat(findMember.getLastModifiedDate()).isNotNull();
        assertThat(findMember.getCreatedBy()).isNull();
        assertThat(findMember.getLastModifiedBy()).isNull();
    }

    @Test
    void 회원가입_실패_이미상용중인_이메일() {
        //given
        String duplicateEmail = "email";
        Member member1 = new Member("member1", duplicateEmail, "password1");
        Member member2 = new Member("member2", duplicateEmail, "password2");

        //when
        memberService.join(member1);

        //then
        assertThatThrownBy(() -> memberService.join(member2)).isInstanceOf(DuplicateEmailException.class);
        assertThat(member2.getId()).isNull();
    }

    @Test
    void 로그인_성공() {
        //given
        Member member = new Member("member1", "email1", "password1");
        memberService.join(member);

        //when
        Optional<Long> result = memberService.login(member.getEmail(), member.getPassword());

        //then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(member.getId());
    }

    @Test
    void 로그인_이메일_불일치() {
        //given
        Member member = new Member("member1", "email1", "password1");
        memberService.join(member);

        //when
        Optional<Long> result = memberService.login(member.getEmail() + "1", member.getPassword());

        //then
        assertThat(result).isEmpty();
    }

    @Test
    void 로그인_비밀번호_불일치() {
        //given
        Member member = new Member("member1", "email1", "password1");
        memberService.join(member);

        //when
        Optional<Long> result = memberService.login(member.getEmail(), member.getPassword() + "1");

        //then
        assertThat(result).isEmpty();
    }

    @Test
    void 로그인_이메일_비밀번호_불일치() {
        //given
        Member member = new Member("member1", "email1", "password1");
        memberService.join(member);

        //when
        Optional<Long> result = memberService.login(member.getEmail() + "1", member.getPassword() + "1");

        //then
        assertThat(result).isEmpty();
    }
}