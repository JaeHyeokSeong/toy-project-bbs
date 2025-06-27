package hello.board.api.comment;

import hello.board.api.comment.dto.AddCommentDto;
import hello.board.api.comment.dto.AddCommentResultDto;
import hello.board.api.comment.dto.DeleteCommentResultDto;
import hello.board.api.comment.dto.UpdateCommentDto;
import hello.board.repository.comment.query.dto.CommentDto;
import hello.board.repository.comment.query.dto.CommentSearchDto;
import hello.board.service.comment.CommentService;
import hello.board.service.comment.dto.UpdateCommentResultDto;
import hello.board.service.comment.query.CommentQueryService;
import hello.board.entity.comment.Comment;
import hello.board.exception.BindingResultException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static hello.board.SessionConst.MEMBER_ID;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentApiController {

    private final CommentService commentService;
    private final CommentQueryService commentQueryService;

    @GetMapping("/comments/{boardId}")
    public Page<CommentDto> findCommentDtoList(@Valid @ModelAttribute CommentSearchDto searchDto,
                                               BindingResult bindingResult,
                                               @PathVariable Long boardId,
                                               @SessionAttribute(value = MEMBER_ID, required = false) Long memberId,
                                               Pageable pageable) {

        log.info("comment 검색 전달되어진 값={}", searchDto);

        if (bindingResult.hasErrors()) {
            log.info("comment 검색 errors={}", bindingResult);
            throw new BindingResultException(bindingResult.getAllErrors());
        }

        return commentQueryService.findAllComments(boardId, memberId, searchDto, pageable);
    }

    @PostMapping("/comment/{boardId}")
    public ResponseEntity<AddCommentResultDto> addComment(@Valid @RequestBody AddCommentDto dto,
                                                          BindingResult bindingResult,
                                                          @PathVariable Long boardId,
                                                          @SessionAttribute(MEMBER_ID) Long memberId) {

        if (bindingResult.hasErrors()) {
            throw new BindingResultException(bindingResult.getAllErrors());
        }

        Comment comment = commentService.addComment(boardId, memberId, dto.getContent(), dto.getParentCommentId());
        return new ResponseEntity<>(new AddCommentResultDto(comment), HttpStatus.CREATED);
    }

    @PutMapping("/comment/{commentId}")
    public ResponseEntity<UpdateCommentResultDto> updateComment(@Valid @RequestBody UpdateCommentDto dto,
                                                                BindingResult bindingResult,
                                                                @SessionAttribute(MEMBER_ID) Long memberId) {

        log.info("comment 수정, 전달되어진 값={}", dto);

        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            throw new BindingResultException(errors);
        }

        UpdateCommentResultDto updatedComment = commentService.changeContent(
                dto.getCommentId(),
                memberId,
                dto.getContent());

        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<DeleteCommentResultDto> deleteComment(@PathVariable Long commentId,
                                                                @SessionAttribute(MEMBER_ID) Long memberId) {

        commentService.deleteComment(commentId, memberId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new DeleteCommentResultDto(commentId, "deleted"));
    }
}
