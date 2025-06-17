package hello.board.domain.service.board;

import hello.board.entity.Slug;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SlugTest {

    @Test
    void test1() {
        //given
        String title = "문제9 정답: np.int32(11)";
        //when
        String result = Slug.generate(title);
        //then
        assertThat(result).isEqualTo("문제9-정답-np-int32-11");
    }

    @Test
    void test2() {
        //given
        String title = "2회 기출유형 작업형1 -2 번 질문";
        //when
        String result = Slug.generate(title);
        //then
        assertThat(result).isEqualTo("2회-기출유형-작업형1-2-번-질문");
    }

    @Test
    void test3() {
        //given
        String title = "로지스틱회귀분석이 강의가 줄어든것 같아요~";
        //when
        String result = Slug.generate(title);
        //then
        assertThat(result).isEqualTo("로지스틱회귀분석이-강의가-줄어든것-같아요");
    }

    @Test
    void test4() {
        //given
        String title = "8:00 영상타임에 df.groupby할때 df에 저장안하는 이유 알수있나요?";
        //when
        String result = Slug.generate(title);
        //then
        assertThat(result).isEqualTo("8-00-영상타임에-df-groupby할때-df에-저장안하는-이유-알수있나요");
    }

    @Test
    void test5() {
        //given
        String title = "질문있어요!";
        //when
        String result = Slug.generate(title);
        //then
        assertThat(result).isEqualTo("질문있어요");
    }

    @Test
    void test6() {
        //given
        String title = "pop 질문";
        //when
        String result = Slug.generate(title);
        //then
        assertThat(result).isEqualTo("pop-질문");
    }

    @Test
    void test7() {
        //given
        String title = "외부설정 @ConfigurationProperties의 내부 클래스에서 static을 사용하는 이유를 알고 싶습니다.";
        //when
        String result = Slug.generate(title);
        //then
        assertThat(result).isEqualTo("외부설정-configurationproperties의-내부-클래스에서-static을-사용하는-이유를-알고-싶습니다");
    }

    @Test
    void test8() {
        //given
        String title = "Thread Config Max 패널 설정 변경 질문";
        //when
        String result = Slug.generate(title);
        //then
        assertThat(result).isEqualTo("thread-config-max-패널-설정-변경-질문");
    }

    @Test
    void test9() {
        //given
        String title = "외부설정 @ConfigurationProperties의 내부 클래스에서 static을 사용하는 이유를 알고 싶습니다.";
        //when
        String result = Slug.generate(title);
        //then
        assertThat(result).isEqualTo("외부설정-configurationproperties의-내부-클래스에서-static을-사용하는-이유를-알고-싶습니다");
    }

    @Test
    void test10() {
        //given
        String title = "application.properties를 프로젝트 내부에 두고 사용한다 하더라도, 여전히 이전 강의와 동일한 문제점이 있는 거 아닌가요?";
        //when
        String result = Slug.generate(title);
        //then
        assertThat(result).isEqualTo("application-properties를-프로젝트-내부에-두고-사용한다-하더라도-여전히-이전-강의와-동일한-문제점이-있는-거-아닌가요");
    }

    @Test
    void test11() {
        //given
        String title = "application.properties를 프로젝트 내부에 두고 사용☺️한다 하더라도, 여전히 이전 강의와 동일한 문제점이 있는 거 아닌가요?";
        //when
        String result = Slug.generate(title);
        //then
        assertThat(result).isEqualTo("application-properties를-프로젝트-내부에-두고-사용-한다-하더라도-여전히-이전-강의와-동일한-문제점이-있는-거-아닌가요");
    }

    @Test
    void test12() {
        //given
        String title = "application.properties를 프로젝트 내부에 두고 사용☺️한다 하더라도, 여전/히 이전 강의와 동일한 문제점이 있는 거 아닌가요?";
        //when
        String result = Slug.generate(title);
        //then
        assertThat(result).isEqualTo("application-properties를-프로젝트-내부에-두고-사용-한다-하더라도-여전-히-이전-강의와-동일한-문제점이-있는-거-아닌가요");
    }

    @Test
    void test13() {
        //given
        String title = "application.properties를 프로젝트 내부에 두고 사용☺️한다 하더라도, 여전//히 이전 강의와 동일한 문제점이 있는 거 아닌가요?";
        //when
        String result = Slug.generate(title);
        //then
        assertThat(result).isEqualTo("application-properties를-프로젝트-내부에-두고-사용-한다-하더라도-여전-히-이전-강의와-동일한-문제점이-있는-거-아닌가요");
    }

    @Test
    void test14() {
        //given
        String title = "Insert JSON text into SQL using C#";
        //when
        String result = Slug.generate(title);
        //then
        assertThat(result).isEqualTo("insert-json-text-into-sql-using-c-sharp");
    }

    @Test
    void test15() {
        //given
        String title = "pred = (pred>0.5).astype(int)";
        //when
        String result = Slug.generate(title);
        //then
        assertThat(result).isEqualTo("pred-pred-gt-0-5-astype-int");
    }

    @Test
    void test16() {
        //given
        String title = "pred = (pred>0.5).astype(int)>=";
        //when
        String result = Slug.generate(title);
        //then
        assertThat(result).isEqualTo("pred-pred-gt-0-5-astype-int-gte");
    }

    @Test
    void test17() {
        //given
        String title = "<pred = (p<=red>0.5)>.ast<ype(int)>=";
        //when
        String result = Slug.generate(title);
        //then
        assertThat(result).isEqualTo("lt-pred-p-lte-red-gt-0-5-gt-ast-lt-ype-int-gte");
    }

    @Test
    void test18() {
        //given
        String title = "it's good";
        //when
        String result = Slug.generate(title);
        //then
        assertThat(result).isEqualTo("it-s-good");
    }

    @Test
    void test19() {
        //given
        String title = "ㄴㄲㅁadㅁ;;ㅏ가ㅁ싸ㄴㅇ";
        //when
        String result = Slug.generate(title);
        //then
        assertThat(result).isEqualTo("ㄴㄲㅁadㅁ-ㅏ가ㅁ싸ㄴㅇ");
    }

}