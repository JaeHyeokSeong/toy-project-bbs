package hello.board.domain.repository.board.query.dto;

import lombok.Data;

@Data
public class BoardSearchCondition {
    private SearchTarget searchTarget; //검색기준
    private String searchTerm; //검색어
    private int size; //페이징 사이즈
    private SearchSort sortBy; //정렬 기준
}
