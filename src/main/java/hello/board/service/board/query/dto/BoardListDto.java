package hello.board.service.board.query.dto;

import hello.board.entity.board.Board;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
public class BoardListDto {

    private Long id;
    private String slug;
    private String title;
    private String name;
    private String createdDate;
    private long views;

    public BoardListDto(Board board) {
        id = board.getId();
        slug = board.getSlug();
        title = board.getTitle();
        name = board.getMember().getName();
        createdDate = board.getCreatedDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        views = board.getViews();
    }
}
