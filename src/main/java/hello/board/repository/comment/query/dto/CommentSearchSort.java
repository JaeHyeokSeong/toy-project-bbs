package hello.board.repository.comment.query.dto;

import lombok.Getter;

@Getter
public enum CommentSearchSort {

    LIKES("좋아요순"), NEWEST("최신순"), OLDEST("오래된순");

    private final String description;

    CommentSearchSort(String description) {
        this.description = description;
    }
}
