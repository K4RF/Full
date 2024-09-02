package solo.blog.controller.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import solo.blog.entity.database.Comment;
import solo.blog.service.jpa.CommentJpaService;

import java.util.List;


@Controller
@RequestMapping("/post/jpa/postList/{postId}/comments")
@RequiredArgsConstructor
public class CommentJpaController {
    private final CommentJpaService commentJpaService;
    @GetMapping
    public String getComments(@PathVariable Long postId, Model model) {
        List<Comment> comments = commentJpaService.getCommentsByPostId(postId);
        model.addAttribute("comments", comments);
        return "post/jpa/post";
    }
    @PostMapping
    public String addComment(@PathVariable Long postId,
                             @RequestParam String author,
                             @RequestParam String comet) {
        commentJpaService.addComment(postId, author, comet);
        return "redirect:/post/jpa/postList/" + postId;
    }
}