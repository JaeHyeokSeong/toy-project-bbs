package hello.board.repository.board.query.dto;

import hello.board.repository.upload_file.query.dto.UploadFileQueryDto;
import hello.board.entity.ReactionType;
import hello.board.entity.RoleType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BoardQueryDto {

    //게시물 정보
    private Long boardId;
    private String boardTitle;
    private String boardContent;
    private Long boardViews;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private Long writerId;
    private String writerName;
    private RoleType writerRoleType;
    private Long totalLikesCount;
    private Long totalDislikesCount;

    //게시물에 업로드된 파일정보
    private List<UploadFileQueryDto> uploadedFiles;

    //현재 이 게시물을 보고 있는 사용자 정보
    private String viewerName;
    private RoleType viewerRoleType;
    private ReactionType viewerReactionType;
}
