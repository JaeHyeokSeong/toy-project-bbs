package hello.board.domain.repository.board.query.dto;

import lombok.Getter;

@Getter
public enum SearchTarget {

    TITLE("제목"), CONTENT("내용"), NAME("등록자명");

    private final String description;

    SearchTarget(String description) {
        this.description = description;
    }
}
