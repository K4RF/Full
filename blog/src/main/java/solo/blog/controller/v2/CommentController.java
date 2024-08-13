package solo.blog.controller.v2;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import solo.blog.service.v2.CommentService;

@Controller
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public String addComment(@RequestParam Long postId,
                             @RequestParam String author,
                             @RequestParam String content) {
        commentService.createComment(author, content, postId);
        return "redirect:/post/basic/postList/" + postId;
    }

    @GetMapping("/{id}")
    public String getComment(@PathVariable Long id, Model model) {
        model.addAttribute("comment", commentService.getCommentById(id).orElseThrow(() -> new IllegalArgumentException("Invalid comment Id:" + id)));
        return "/comment/commentForm";
    }

    @DeleteMapping("/{id}")
    public String deleteComment(@PathVariable Long id, @RequestParam Long postId) {
        commentService.deleteComment(id);
        return "redirect:/post/basic/postList/" + postId;
    }
}
