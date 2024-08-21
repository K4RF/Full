package solo.blog.controller.v3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import solo.blog.entity.v2.Comment;
import solo.blog.service.v2.CommentServiceV2;

import java.util.List;

@Controller
@RequestMapping("/post/basic/postList/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentServiceV2 commentServiceV2;
    @GetMapping
    public String getComments(@PathVariable Long postId, Model model) {
        List<Comment> comments = commentServiceV2.getCommentsByPostId(postId);
        model.addAttribute("comments", comments);
        return "post/basic/post";
    }
    @PostMapping
    public String addComment(@PathVariable Long postId,
                             @RequestParam String author,
                             @RequestParam String comet) {
        commentServiceV2.addComment(postId, author, comet);
        return "redirect:/post/basic/postList/" + postId;
    }
}