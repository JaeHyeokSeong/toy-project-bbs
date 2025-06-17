package hello.board.domain.repository.board.query.dto;

import lombok.Getter;

@Getter
public enum SearchSort {

    NEWEST("최신순"), OLDEST("오래된순");

    private final String description;

    SearchSort(String description) {
        this.description = description;
    }
}
