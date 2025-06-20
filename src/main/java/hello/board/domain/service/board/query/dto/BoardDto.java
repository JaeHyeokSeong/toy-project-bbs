package hello.board.domain.service.board.query.dto;

import hello.board.domain.service.upload_file.dto.UploadFileDto;
import hello.board.entity.Board;
import hello.board.entity.ReactionType;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
public class BoardDto {
    //게시물 정보
    private Long id;
    private String title;
    private String content;
    private long views;
    private String createdDate;
    private LocalDateTime lastModifiedDate;
    private List<UploadFileDto> uploadedFiles;
    private long totalReactionCount;

    //회원 정보
    private Long memberId;
    private String name;
    private ReactionType reactionType;

    public BoardDto(Board board) {
        //게시물 정보 초기화
        id = board.getId();
        title = board.getTitle();
        content = board.getContent();
        views = board.getViews();
        createdDate = board.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        lastModifiedDate = board.getLastModifiedDate();
        uploadedFiles = board.getUploadFiles()
                .stream()
                .map(UploadFileDto::new)
                .toList();

        //회원 정보 초기화
        name = board.getMember().getName();
        memberId = board.getMember().getId();
    }
}
