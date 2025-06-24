package hello.board.api.comment;

import hello.board.api.comment.dto.*;
import hello.board.domain.repository.comment.query.dto.CommentDto;
import hello.board.domain.repository.comment.query.dto.CommentSearchDto;
import hello.board.domain.service.comment.CommentService;
import hello.board.domain.service.comment.dto.UpdateCommentResultDto;
import hello.board.domain.service.comment.query.CommentQueryService;
import hello.board.entity.Comment;
import hello.board.exception.BindingResultException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/comments-total-count/{boardId}")
    public CommentTotalCountDto commentTotalCount(@RequestParam(required = false) Long parentCommentId,
                                                  @PathVariable Long boardId) {

        Long count = commentQueryService.totalCount(boardId, parentCommentId);
        return new CommentTotalCountDto(boardId, count);
    }

    @GetMapping("/comments/{boardId}")
    public Slice<CommentDto> findCommentDtoList(@Valid @ModelAttribute CommentSearchDto searchDto,
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
}
