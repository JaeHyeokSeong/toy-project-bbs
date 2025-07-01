package hello.board.controller.board;

import hello.board.SessionConst;
import hello.board.repository.board.query.dto.BoardQueryDto;
import hello.board.repository.board.query.dto.BoardSearchCondition;
import hello.board.repository.board.query.dto.SearchSort;
import hello.board.repository.board.query.dto.SearchTarget;
import hello.board.repository.comment.query.dto.CommentSearchSort;
import hello.board.service.board.BoardService;
import hello.board.service.board.query.BoardQueryService;
import hello.board.service.board.query.dto.BoardListDto;
import hello.board.service.board.query.dto.BoardUpdateDto;
import hello.board.service.member.MemberService;
import hello.board.entity.board.Board;
import hello.board.entity.file.FileStore;
import hello.board.exception.BoardNotFoundException;
import hello.board.controller.board.dto.AddBoardDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/bbs")
public class BoardController {

    private final BoardService boardService;
    private final BoardQueryService boardQueryService;
    private final FileStore fileStore;
    private final MemberService memberService;

    @GetMapping
    public String boards(@ModelAttribute("condition") BoardSearchCondition condition,
                         Model model,
                         @PageableDefault Pageable pageable) {
        log.info("전달되어진 검색어들={}", condition);
        log.info("pageable={}", pageable);
        Page<BoardListDto> page = boardQueryService.findBoardListDto(condition, pageable);
        model.addAttribute("page", page);
        model.addAttribute("searchTargets", SearchTarget.values());
        model.addAttribute("searchSorts", SearchSort.values());
        return "index";
    }

    @GetMapping("/{id}/{slug}")
    public String board(@PathVariable Long id, @PathVariable String slug, Model model,
                        @SessionAttribute(value = SessionConst.MEMBER_ID, required = false) Long memberId,
                        HttpServletRequest request, HttpServletResponse response) {
        log.info("id={}, slug={}", id, slug);

        //쿠키에서 question_viewed_{id} 이름으로 조회했을때에, 조회가 되어지면 이미 본 게시물이란 의미.
        //따라서 조회수를 증가하지 않는다, 반대로 조회가 안되어진 경우에는 새로운 게시물이란 의미.
        int count = getCount(id, request, response);
        log.info("조회수 증가되어질 count={}", count);

        BoardQueryDto boardQueryDto = boardQueryService.findBoardQueryDto(id, slug, count, memberId)
                .orElseThrow(BoardNotFoundException::new);
        model.addAttribute("dto", boardQueryDto);

        //comment 정렬 조건
        model.addAttribute("commentSearchSorts", CommentSearchSort.values());

        return "board/board";
    }

    @GetMapping("/write")
    public String addBoardForm(@ModelAttribute("addBoardDto") AddBoardDto dto) {
        return "board/boardForm";
    }

    @PostMapping("/write")
    public String addBoard(@Valid @ModelAttribute AddBoardDto dto, BindingResult bindingResult,
                           @SessionAttribute(value = SessionConst.MEMBER_ID) Long memberId,
                           RedirectAttributes redirectAttributes) {

        log.info("작성되어진 게시물 입력값들={}", dto);

        if (bindingResult.hasErrors()) {
            log.info("작성되어진 게시물 검증 오류메시지={}", bindingResult);
            return "board/boardForm";
        }

        Board board = boardService.saveBoard(
                memberId, dto.getTitle(),
                dto.getContent(),
                dto.getStoreFileNames()
        );
        redirectAttributes.addAttribute("id", board.getId());
        redirectAttributes.addAttribute("slug", board.getSlug());

        return "redirect:/bbs/{id}/{slug}";
    }

    @GetMapping("/edit/{id}")
    public String editBoardForm(@PathVariable Long id,
                                @SessionAttribute(SessionConst.MEMBER_ID) Long memberId, Model model) {

        BoardUpdateDto boardUpdateDto = boardQueryService.findBoardUpdateDto(id, memberId);
        model.addAttribute("boardUpdateDto", boardUpdateDto);
        return "board/editBoard";
    }

    @PostMapping("/edit/{boardId}")
    public String editBoard(
            @Valid @ModelAttribute BoardUpdateDto boardUpdateDto, BindingResult bindingResult,
            @PathVariable Long boardId, @SessionAttribute(SessionConst.MEMBER_ID) Long memberId,
            RedirectAttributes redirectAttributes) {

        log.info("게시판 수정 - 전달되어진 값={}", boardUpdateDto);

        if (bindingResult.hasErrors()) {
            log.info("게시판 수정 - 검증 오류={}", bindingResult);
            return "board/editBoard";
        }

        String slug = boardService.updateBoard(
                boardId,
                memberId,
                boardUpdateDto.getTitle(),
                boardUpdateDto.getContent(),
                boardUpdateDto.getStoreFileNames(),
                boardUpdateDto.getDeleteFileNames()
        );

        redirectAttributes.addAttribute("boardId", boardId);
        redirectAttributes.addAttribute("slug", slug);

        return "redirect:/bbs/{boardId}/{slug}";
    }

    @PostMapping("/delete/{id}")
    public String deleteBoard(@PathVariable Long id,
                              @SessionAttribute(SessionConst.MEMBER_ID) Long memberId) {

        boardService.deleteBoard(id, memberId);
        return "redirect:/bbs";
    }

    /**
     * 조회수를 증가시켜야 하는지, 말아야 하는지를 알아냄.
     */
    public int getCount(Long id, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String boardViewedName = "board_viewed_" + id;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                boolean isViewed = cookie.getName().equals(boardViewedName);
                if (isViewed) {
                    return 0;
                }
            }
        }

        Cookie cookie = new Cookie(boardViewedName, "1");
        cookie.setMaxAge(3600); //1시간
        response.addCookie(cookie);
        return 1;
    }
}
