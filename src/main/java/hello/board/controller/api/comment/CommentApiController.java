package hello.board.controller.api.comment;

import hello.board.controller.api.comment.dto.AddCommentDto;
import hello.board.controller.api.comment.dto.AddCommentResultDto;
import hello.board.controller.api.comment.dto.DeleteCommentResultDto;
import hello.board.controller.api.comment.dto.UpdateCommentDto;
import hello.board.dto.ResponseData;
import hello.board.dto.ResponseResult;
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
    public ResponseResult findCommentDtoList(@Valid @ModelAttribute CommentSearchDto searchDto,
                                             BindingResult bindingResult,
                                             @PathVariable Long boardId,
                                             @SessionAttribute(value = MEMBER_ID, required = false) Long memberId,
                                             Pageable pageable) {

        log.info("comment 검색 전달되어진 값={}", searchDto);

        if (bindingResult.hasErrors()) {
            log.info("comment 검색 errors={}", bindingResult);
            throw new BindingResultException(bindingResult.getAllErrors());
        }

        ResponseData responseData = commentQueryService.findAllComments(boardId, memberId, searchDto, pageable);
        return new ResponseResult(HttpStatus.OK.toString(), "", responseData);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/comment/{boardId}")
    public ResponseResult addComment(@Valid @RequestBody AddCommentDto dto,
                                                          BindingResult bindingResult,
                                                          @PathVariable Long boardId,
                                                          @SessionAttribute(MEMBER_ID) Long memberId) {

        if (bindingResult.hasErrors()) {
            throw new BindingResultException(bindingResult.getAllErrors());
        }

        Comment comment = commentService.addComment(boardId, memberId, dto.getContent(), dto.getParentCommentId());

        return new ResponseResult(HttpStatus.CREATED.toString(), "comment 등록 완료.", new AddCommentResultDto(comment));
    }

    @PutMapping("/comment/{commentId}")
    public ResponseResult updateComment(@Valid @RequestBody UpdateCommentDto dto,
                                                                BindingResult bindingResult,
                                                                @SessionAttribute(MEMBER_ID) Long memberId,
                                                                @PathVariable Long commentId) {

        log.info("comment 수정, 전달되어진 값={}", dto);

        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            throw new BindingResultException(errors);
        }

        UpdateCommentResultDto updatedComment = commentService.changeContent(
                commentId,
                memberId,
                dto.getContent());

        return new ResponseResult(HttpStatus.OK.toString(), "comment 수정 완료.", updatedComment);
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseResult deleteComment(@PathVariable Long commentId,
                                                                @SessionAttribute(MEMBER_ID) Long memberId) {

        commentService.deleteComment(commentId, memberId);

        return new ResponseResult(HttpStatus.OK.toString(), "comment 삭제 완료.", new DeleteCommentResultDto(commentId, "deleted"));
    }
}
