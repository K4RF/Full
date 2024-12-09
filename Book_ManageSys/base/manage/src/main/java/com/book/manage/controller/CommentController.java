package com.book.manage.controller;

import com.book.manage.entity.Comment;
import com.book.manage.service.book.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/bookList/{bookId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public String getComments(@PathVariable Long bookId, Model model) {
        List<Comment> comments = commentService.getCommentsByBookId(bookId);
        model.addAttribute("comments", comments);
        return "book/bookInfo";
    }
}
