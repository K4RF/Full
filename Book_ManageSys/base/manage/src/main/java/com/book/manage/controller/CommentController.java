package com.book.manage.controller;

import com.book.manage.entity.Comment;
import com.book.manage.entity.Member;
import com.book.manage.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/bookList/{bookId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public String getComments(@PathVariable Long bookId, Model model){
        List<Comment> comments = commentService.getCommentsByBookId(bookId);

        model.addAttribute("comments", comments);
        return "book/bookInfo";
    }

    @PostMapping
    public String addComment(@PathVariable Long bookId,
                             @RequestParam String review,
                             @RequestParam BigDecimal rating,
                             Model model,
                             @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        // 로그인 여부 확인
        if (loginMember == null) {
            return "redirect:/login?redirectURL=/bookList/" + bookId;
        }

        // 댓글 작성 확인
        boolean hasComment = commentService.hasUserCommented(bookId, loginMember);
        if (hasComment) {
            model.addAttribute("error", "이미 작성된 도서입니다.");
        }
        model.addAttribute("hasComment", hasComment);
        //댓글 작성
        commentService.addComment(bookId, loginMember.getNickname(), review, rating);
        return "redirect:/bookList/" + bookId;
    }

    @PostMapping("/{commentId}/edit")
    public String editComment(@PathVariable Long bookId,
                              @PathVariable Long commentId,
                              @RequestParam String newReview,
                              @RequestParam BigDecimal newRating,
                              @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        if (loginMember == null) {
            return "redirect:/login?redirectURL=/bookList/" + bookId;
        }
        commentService.updateComment(commentId, newReview, loginMember.getNickname(), newRating);
        return "redirect:/bookList/" + bookId;
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long bookId,
                                @PathVariable Long commentId,
                                @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        if (loginMember == null) {
            return "redirect:/login?redirectURL=/bookList/" + bookId;
        }
        // 댓글 삭제 로직 실행
        commentService.deleteComment(commentId, loginMember.getNickname());
        return "redirect:/bookList/" + bookId;
    }
}
