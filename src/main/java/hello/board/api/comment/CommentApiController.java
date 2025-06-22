package hello.board.api.comment;

import hello.board.api.comment.dto.AddCommentParentDto;
import hello.board.api.comment.dto.AddCommentParentResponseDto;
import hello.board.domain.repository.comment.query.dto.CommentParentDto;
import hello.board.domain.service.comment.CommentService;
import hello.board.domain.service.comment.query.CommentQueryService;
import hello.board.entity.Comment;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static hello.board.SessionConst.MEMBER_ID;

@RestController
@RequiredArgsConstructor
public class CommentApiController {

    private final CommentService commentService;
    private final CommentQueryService commentQueryService;

    @GetMapping("/api/comments-parent/{boardId}")
    public Slice<CommentParentDto> commentParentDtoList(@PathVariable Long boardId, Pageable pageable) {
        return commentQueryService.findAllCommentsParent(boardId, pageable);
    }

    @ResponseStatus
    @PostMapping("/api/comment-parent/{boardId}")
    public ResponseEntity<AddCommentParentResponseDto> commentParent(@Valid @RequestBody AddCommentParentDto dto,
                                                                     BindingResult bindingResult,
                                                                     @PathVariable Long boardId,
                                                                     @SessionAttribute(MEMBER_ID) Long memberId) {

        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("comment의 내용은 필수입니다.");
        }

        Comment comment = commentService.addComment(boardId, memberId, dto.getContent(), null);
        return new ResponseEntity<>(new AddCommentParentResponseDto(comment), HttpStatus.CREATED);
    }
}
